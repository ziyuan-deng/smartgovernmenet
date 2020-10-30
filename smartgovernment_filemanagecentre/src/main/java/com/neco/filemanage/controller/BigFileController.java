package com.neco.filemanage.controller;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.neco.common.response.ResponseUtil;
import com.neco.filemanage.constant.FileConstant;
import com.neco.filemanage.enums.BigFileEnum;
import com.neco.filemanage.event.MergeBigFileEvent;
import com.neco.filemanage.service.FileAssignService;
import com.neco.filemanage.service.FileService;
import com.neco.filemanage.service.FileSystemParamService;
import com.neco.filemanage.utils.FileType;
import com.neco.filemanage.utils.FilesUtils;
import com.neco.filemanagecentre.model.BigFiles;
import com.neco.filemanagecentre.model.CentreFileInfo;
import com.neco.filemanagecentre.model.FileAssignInfo;
import com.neco.filemanagecentre.vo.FileSystemParamVo;
import com.neco.sglog.utils.SpringContextHolder;
import com.neco.utils.ComUtil;
import com.neco.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 大文件上传下载操作处理
 * @author ziyuan_deng
 * @date 2020/10/9
 */
@Slf4j
@RestController
@RequestMapping("/bigFile")
public class BigFileController  {

    @Value("${bigFile.localPath}")
    private String localPath;

    private Long redisLongTime = 60*60*24L;

    //private final long downloadFileSize = 2*1024;
    //private final int HAST_INT = 7;

    @Autowired
    private FileService fileService;
    @Autowired
    private FileSystemParamService paramService;
    @Autowired
    private FileAssignService assignService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private AppendFileStorageClient fileStorageClient;
    @Autowired
    private FastFileStorageClient storageClient;
    /**
     * 大文件上传
     * @param file
     * @param bigFiles 分块文件上传对象
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ModelMap upload(MultipartFile file, BigFiles bigFiles, HttpServletResponse response, HttpServletRequest request) {
        try {
            String  identifier = bigFiles.getIdentifier();
            if(identifier ==null || "".equals(identifier)){
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return ResponseUtil.retErrorInfo("identifie为空");
            }
            String fileName = file.getOriginalFilename();
            //文件后缀名
            String postfix = fileName.substring(fileName.lastIndexOf(".") + 1);
            FileSystemParamVo paramVo = paramService.selectSystemParamList();
            if (paramVo != null) {
                Long supportSize = paramVo.getFileSize();
                if (bigFiles.getTotalSize()>supportSize){
                    return ResponseUtil.retErrorInfo(FileConstant.FILESYSTEM_LIMITSIZE_MSG);
                }
                if (!paramVo.getTypeList().contains(postfix)) {
                    return ResponseUtil.retErrorInfo("本系统不支持文件类型为："+postfix+" 的上传！");
                }
            }
            //判断单位或者应用的分配额度
            if (StringUtils.isNotBlank(bigFiles.getCompanyId()) || StringUtils.isNotBlank(bigFiles.getAppId())) {
                FileAssignInfo assignInfo = assignService.selectByCompanyIdOrAppId(bigFiles.getCompanyId(),bigFiles.getAppId());
                Long usedSize = assignInfo.getUsedSize();
                Long assignSize = assignInfo.getAssignSize();
                Long unuseSize = assignSize - usedSize;
                if (unuseSize<bigFiles.getTotalSize()) {
                    return ResponseUtil.retErrorInfo(FileConstant.COMPANYORAPP_IMITSIZE_MSG);
                }
            }
            //临时文件夹
            File parentFileDir = new File(localPath+identifier);
            //已保存分片区块
            String bigFileTotalKey= BigFileEnum.BIG_FILE_TOTAL.getValue()+identifier;
            //已保存files对象key
            String bigFileKey = BigFileEnum.BIG_FILE.getValue()+ identifier;
            //保存DFS追加文件块数
            String bigFileNumberKey = BigFileEnum.BIG_FILE_NUMBER.getValue()+ identifier;
            if(!parentFileDir.exists()){
                parentFileDir.mkdirs();
            }
            //如果是第一个文件块，先保存到dfs服务器作为开头
            if(bigFiles.getChunkNumber()==1) {
                InputStream inputStream = file.getInputStream();
                //上传
                StorePath storePath = fileStorageClient.uploadAppenderFile(null, inputStream, file.getSize(), fileName.substring(fileName.lastIndexOf(".") + 1));
                String url = storePath.getFullPath();
                // 获取当前用户信息
               // AwsUserInfo user = getCurrUser(request);
               // CentreFileInfo sysFile = CentreFileInfo.builder().fileName(fileName).fileSize(file.getSize()).build();
                String fid = ComUtil.randomUUID();
                CentreFileInfo sysFile = CentreFileInfo.builder()
                        .id(fid)
                        .appId(bigFiles.getAppId())
                        .companyId(bigFiles.getCompanyId())
                        .companyName(bigFiles.getCompanyName())
                        .departmentId(bigFiles.getDepartmentId())
                        .departmentName(bigFiles.getDepartmentName())
                        .fileName(fileName)
                        .fileSize(bigFiles.getTotalSize())
                        .fileType(Integer.parseInt(FileType.fileType(fileName)))
                        .createTime(new Date())
                        .updateTime(new Date())
                        .postfix(postfix)
                        .mimeType(FilesUtils.getMimeType(file.getOriginalFilename()))
                        .publicStatus(bigFiles.getPublicStatus())
                        .downloadCount(0)
                        .url(url)
                        .uploadUserId(bigFiles.getUploadUserId())
                        .uploadUserName(bigFiles.getUploadUserName())
                        .visitCount(0).version(0).sourceFileId(fid).build();
                sysFile.setGroupName(storePath.getGroup());
                sysFile.setRemoteFileName(storePath.getPath());
                if (fileService.insert(sysFile)) {
                    redisUtil.set(bigFileKey, sysFile,redisLongTime);
                    //开始存入下标
                    redisUtil.set(bigFileNumberKey,0,redisLongTime);
                    List<Object> list = redisUtil.lGet(bigFileTotalKey,0,-1);
                    if(bigFiles.getTotalChunks()-1 == list.size()){
                        //list的value从2开始，区块从1开始并放入dfs
                        bigFiles.setTempPath(parentFileDir);
                        SpringContextHolder.publishEvent(new MergeBigFileEvent(bigFiles));
                        //BigFileUtils.bigUpload(bigFiles,parentFileDir,redisUtil);
                    }
                }
            }else{
                //保存到临时文件夹
                File tempPartFile = new File(parentFileDir, bigFiles.getFilename() + "_" + bigFiles.getChunkNumber() + ".part");
                FileUtils.copyInputStreamToFile(file.getInputStream(), tempPartFile);
                //添加一条添加记录
                redisUtil.lSet(bigFileTotalKey,bigFiles.getChunkNumber(),redisLongTime);
                List<Object> list = redisUtil.lGet(bigFileTotalKey,0,-1);
                if( bigFiles.getTotalChunks()-1 == list.size()){
                    //list的value从2开始，区块从1开始并放入dfs
                    //异步实现合并文件夹
                    bigFiles.setTempPath(parentFileDir);
                    SpringContextHolder.publishEvent(new MergeBigFileEvent(bigFiles));
                   // BigFileUtils.bigUpload(bigFiles,parentFileDir,redisUtil);
                }
            }
            return ResponseUtil.retCorrectModel(FileConstant.UPLOAD_SUCCESS);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ResponseUtil.retErrorInfo(FileConstant.UPLOAD_FAIL);
        }
    }

    /**
     * 大文件上传重复分片校验
     * @param bigFiles
     * @param response
     * @return
     */
    @RequestMapping(value = "/uploadCheck", method = RequestMethod.GET)
    public ModelMap uploadCheck(BigFiles bigFiles,HttpServletResponse response){
        Map<String,Boolean> map = new HashMap(1);
        try {
            //已保存分片区块
            String bigFileTotalKey= BigFileEnum.BIG_FILE_TOTAL.getValue()+bigFiles.getIdentifier();
            List<Object> list = redisUtil.lGet(bigFileTotalKey,0,-1);
            if (list !=null && list.stream().anyMatch(o -> bigFiles.getChunkNumber().equals(Integer.valueOf(o.toString())))){
                //已上传过返回200
                map.put("skipUpload",true);
                return ResponseUtil.retCorrectModel(map);
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        //没上传过返回205,
        map.put("skipUpload",false);
        response.setStatus(HttpStatus.RESET_CONTENT.value());
        return ResponseUtil.retCorrectModel(map);
    }




}
