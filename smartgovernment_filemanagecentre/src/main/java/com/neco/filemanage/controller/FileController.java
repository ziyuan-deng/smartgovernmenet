package com.neco.filemanage.controller;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.FileInfo;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.neco.common.response.ResponseUtil;
import com.neco.common.web.BaseController;
import com.neco.common.web.SgUser;
import com.neco.filemanage.clients.MessageClient;
import com.neco.filemanage.service.FileAssignService;
import com.neco.filemanage.service.FileService;
import com.neco.filemanage.service.FileSystemParamService;
import com.neco.filemanage.utils.FileType;
import com.neco.filemanage.utils.FilesUtils;
import com.neco.filemanagecentre.dto.FilesDto;
import com.neco.filemanagecentre.model.CentreFileInfo;
import com.neco.filemanagecentre.model.FileAssignInfo;
import com.neco.filemanagecentre.vo.FileSystemParamVo;
import com.neco.messagecentre.dto.MessageDto;
import com.neco.sglog.annotation.SgLog;
import com.neco.utils.ComUtil;
import com.neco.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 文件上传/下载接口
 *
 * @author ziyuan_deng
 * @create 2020-09-06 20:42
 */
@Api(value = "FileController", description = "文件操作综合接口")
@RequestMapping("/file")
@RestController
@Slf4j
public class FileController extends BaseController {

    private final long downloadFileSize = 2*1024;

    @Autowired
    private FileService fileService;
    @Autowired
    private FileSystemParamService paramService;
    @Autowired
    private FileAssignService assignService;

    @Autowired
    private FastFileStorageClient storageClient;
    /*@Autowired
    private TrackerClient trackerClient;*/
    @Autowired
    private AppendFileStorageClient fileStorageClient;
    @Autowired
    private FdfsWebServer fdfsWebServer;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MessageClient messageClient;

    @ApiOperation(value = "test", notes = "资媒中心")
    @GetMapping("/testlog")
    @SgLog(module = "文件管理中心",description = "测试")
    public String test(@RequestParam("num") String num,HttpServletRequest request){
        SgUser currUser = getCurrUser(request);
        log.error("测试日志：",num);
        MessageDto messageDto = new MessageDto();
        messageDto.setDurable(true);
        messageDto.setExchangeName("com.rabbit.message.topic");
        messageDto.setRoutingKey("test.topic.key");
        messageDto.setParams(num);
        String result = messageClient.sendMessageByObj(messageDto);
        return result;
    }

    @ApiOperation(value = "文件管理中心", notes = "文件查询列表")
    @PostMapping("/list")
    public ModelMap list(@RequestBody FilesDto filesDto){
        if (filesDto == null) {
            filesDto = new FilesDto();
            filesDto.setPageNum(0);
            filesDto.setPageSize(10);
            //filesDto.setPublicStatus(1);
        }else{
            filesDto.setPageNum(filesDto.getPageNum()==null ? 0 : filesDto.getPageNum());
            filesDto.setPageSize(filesDto.getPageSize()==null ? 10 : filesDto.getPageSize());
           // filesDto.setPublicStatus(1);
        }
        return ResponseUtil.retCorrectModel(fileService.selectFilesByPage(filesDto));
    }

    @ApiOperation(value = "文件管理中心", notes = "根据ID获取文件信息")
    @PostMapping("/getFileById")
    public ModelMap getFileById(String id){
        if (StringUtils.isBlank(id)) {
            return ResponseUtil.retErrorInfo("参数为空!");
        }
        return ResponseUtil.retCorrectModel(fileService.selectById(id));
    }

    @ApiOperation(value = "文件管理中心", notes = "根据ID更新文件信息")
    @PostMapping("/updateFileById")
    public ModelMap updateFileInfoById(@RequestBody FilesDto filesDto){
        if (filesDto == null) {
            return ResponseUtil.retErrorInfo("参数为空!");
        }
        CentreFileInfo fileInfo = new CentreFileInfo();
        BeanUtils.copyProperties(filesDto,fileInfo);
        fileInfo.setUpdateTime(new Date());
        return ResponseUtil.retCorrectModel(fileService.updateById(fileInfo));
    }

    /**
     * fastDFS上传文件的方法
     * @param file
     * @param filesDto
     * @return
     */
    @ApiOperation(value = "上传文件", notes = "")
    @SgLog(module = "文件管理中心",description = "上传文件")
    @PostMapping("/upload")
    @ResponseBody
    public String uploadFastdfs(MultipartFile file, FilesDto filesDto, HttpServletRequest request) throws IOException {
        if (file.isEmpty()) {
            return ResponseUtil.retErrorJson("上传文件参数错误");
        }
        long fileSize = file.getSize();
        //源文件名称
        String fileName = file.getOriginalFilename();
        //文件后缀名
        String postfix = fileName.substring(fileName.lastIndexOf(".") + 1);
        FileSystemParamVo paramVo = paramService.selectSystemParamList();
        if (paramVo != null) {
            Long supportSize = paramVo.getFileSize();
            if (fileSize>supportSize){
                return ResponseUtil.retErrorJson("上传文件大于1G，请上传到本系统的文件大小要小于1G!");
            }
            if (!paramVo.getTypeList().contains(postfix)) {
                return ResponseUtil.retErrorJson("本系统不支持文件类型为："+postfix+" 的上传！");
            }
        }
        //判断单位或者应用的分配额度
        if (StringUtils.isNotBlank(filesDto.getCompanyId()) || StringUtils.isNotBlank(filesDto.getAppId())) {
            FileAssignInfo assignInfo = assignService.selectByCompanyIdOrAppId(filesDto.getCompanyId(),filesDto.getAppId());
            Long usedSize = assignInfo.getUsedSize();
            Long assignSize = assignInfo.getAssignSize();
            Long unuseSize = assignSize - usedSize;
            if (unuseSize<fileSize) {
                return ResponseUtil.retErrorJson("你上次的文件太大超出该单位或者应用未使用的分配额度！");
            }
        }
        MultipartFile multipartFile = file;
        String[] fileAbsolutePath = {};
        String filePath = file.getOriginalFilename();
        InputStream inputStream = multipartFile.getInputStream();
        StorePath storePath = null;
        try {
            //调用fastDFS封装上传方法
             storePath = storageClient.uploadFile(inputStream, file.getSize(), postfix, null);
            log.info("fileAbsolutePath==={}", fileAbsolutePath.toString());
        } catch (Exception e) {
            log.error("上传fastDFS失败！", e);
        }
        String path =  storePath.getPath();
        //文件存储的url
        String url = fdfsWebServer.getWebServerUrl() + storePath.getFullPath();
        //上传文件信息赋值
        String fid = ComUtil.randomUUID();
        CentreFileInfo sysFile = CentreFileInfo.builder()
                .id(fid)
                .appId(filesDto.getAppId())
                .companyId(filesDto.getCompanyId())
                .companyName(filesDto.getCompanyName())
                .departmentId(filesDto.getDepartmentId())
                .departmentName(filesDto.getDepartmentName())
                .fileName(fileName)
                .fileSize(fileSize)
                .fileType(Integer.parseInt(FileType.fileType(fileName)))
                .createTime(new Date())
                .updateTime(new Date())
                .postfix(postfix)
                .mimeType(FilesUtils.getMimeType(filePath))
                .publicStatus(filesDto.getPublicStatus())
                .downloadCount(0)
                .url(url)
                .uploadUserId(filesDto.getUploadUserId())
                .uploadUserName(filesDto.getUploadUserName())
                .visitCount(0).version(0).sourceFileId(fid).build();

        sysFile.setGroupName(storePath.getGroup());
        sysFile.setRemoteFileName(path);
        if (fileService.insert(sysFile)) {
            return ResponseUtil.retCorrectJson(sysFile);
        } else {
            return ResponseUtil.retErrorJson("上传成功，保存数据库失败");
        }
    }


    @ApiOperation(value = "删除文件", notes = "")
    @SgLog(module = "资媒中心",description = "删除文件")
    @DeleteMapping("/removeFile")
    @ResponseBody
    public ModelMap removeFile(String id) throws IOException {
        CentreFileInfo files = fileService.selectById(id);
        if (null == files || files.equals("")) {
            return ResponseUtil.retErrorInfo("删除的文件不在！");
        }
        String groupName = files.getGroupName();
        String remoteFileName = files.getRemoteFileName();
        String url = files.getUrl();
        try {
            storageClient.deleteFile(groupName,remoteFileName);
            boolean flag = fileService.deleteById(id);
            return ResponseUtil.retCorrectInfo("删除文件成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.retErrorInfo("删除文件失败！");
        }
    }
    /**
     * fastDFS单个文件下载的方法
     *
     * @param request
     * @param response
     * @param id  文件id
     */
    @ApiOperation(value = "单个文件下载", notes = "")
    @SgLog(module = "资源文件中心",description = "单个文件下载")
    @GetMapping("/downloadFile")
    public ModelMap downloadFile(HttpServletRequest request, HttpServletResponse response, String id) throws IOException {
        CentreFileInfo files = fileService.selectById(id);
        if (null == files) {
            return null;
        }
        ServletOutputStream out = response.getOutputStream();
        String filename = files.getFileName();//得到文件名
        String groupName = files.getGroupName();
        String remoteFileName = files.getRemoteFileName();
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("filename", filename);
        try {
           //获取fastDFS的文件信息
            FileInfo info = fileStorageClient.queryFileInfo(files.getGroupName(), files.getRemoteFileName());
            if (info ==null || info.getFileSize()==0){
                return null;
            }
            long fileInfoFileSize = info.getFileSize();
            //启用大文件下载
            if (fileInfoFileSize>downloadFileSize){
                long i = 0;
                while (i<fileInfoFileSize){
                    long downloadBytes = i+ downloadFileSize <fileInfoFileSize? downloadFileSize :fileInfoFileSize-i;
                    byte[] bytes = storageClient.downloadFile(groupName, remoteFileName, i, downloadBytes, new DownloadByteArray());
                    if (bytes==null){
                        break;
                    }
                    out.write(bytes);
                    i += downloadFileSize;
                }
                out.flush();
                boolean flag = updateDownloadCount(files);
                return  ResponseUtil.retCorrectInfo("文件下载成功！");
            }
            byte[] bytes = storageClient.downloadFile(groupName, remoteFileName,new DownloadByteArray());
            out.write(bytes);
            out.flush();
            boolean flag = updateDownloadCount(files);
            log.info("下载成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseUtil.retErrorInfo("文件下载失败！");
        } finally {
            out.close();
        }
        return ResponseUtil.retCorrectInfo("文件下载成功！");
    }

    /**
     * 更新下载次数
     * @param files
     * @return
     */
    private boolean updateDownloadCount(CentreFileInfo files) {
        Integer downloadCount = files.getDownloadCount();
        if (downloadCount == null) {
            downloadCount = 0;
        }
        downloadCount = downloadCount + 1;
        files.setDownloadCount(downloadCount);
        boolean flag = fileService.updateById(files);
        return flag;
    }

    /**
     * fastdfs批量下载的方法
     *
     * @param ids
     * @return
     */
    @ApiOperation(value = "批量文件下载")
    @SgLog(module = "资源文件中心",description = "批量文件下载")
    @GetMapping("/downloadFiles")
    public String batchDownFile(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "ids", required = true) List<String> ids) throws IOException {
        log.info("ids:" + ids);
        List<CentreFileInfo> fileList = fileService.selectBatchIds(ids);
        if (null == fileList || fileList.isEmpty()) {
            log.info("下载的文件不在");
            return ResponseUtil.retCorrectJson("下载的文件不在");
        }
        String fileFirstName = fileList.get(0).getFileName();
        String zipFileName = fileFirstName.substring(0, fileFirstName.lastIndexOf(".")) + "等.zip";
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(zipFileName, "UTF-8"));

        //设置压缩流：直接写入response，实现边压缩边下载
        ZipOutputStream zipos = null;
        try {
            zipos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
            zipos.setMethod(ZipOutputStream.DEFLATED);//设置压缩方法
        } catch (Exception e) {
            e.printStackTrace();
        }
        DataOutputStream os = null;
        for (int i = 0; i < fileList.size(); i++) {

            CentreFileInfo files = fileList.get(i);
            String groupName = files.getGroupName();
            String remoteFileName = files.getRemoteFileName();
            try {
                //添加ZipEntry，并ZipEntry中写入文件流
                zipos.putNextEntry(new ZipEntry(fileList.get(i).getFileName()));
                os = new DataOutputStream(zipos);
                byte[] bytes = storageClient.downloadFile(groupName, remoteFileName, new DownloadByteArray());
                InputStream is = new ByteArrayInputStream(bytes);
                byte[] b = new byte[1024];
                int length = 0;
                while ((length = is.read(b)) != -1) {
                    os.write(b, 0, length);
                }
                boolean flag = updateDownloadCount(files);
                is.close();
                zipos.closeEntry();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            os.flush();
            os.close();
            zipos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseUtil.retCorrectJson("下载成功！");
    }


}
