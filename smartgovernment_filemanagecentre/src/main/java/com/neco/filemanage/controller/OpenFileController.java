package com.neco.filemanage.controller;

import com.github.tobato.fastdfs.domain.fdfs.FileInfo;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.neco.common.response.ResponseUtil;
import com.neco.filemanage.service.FileService;
import com.neco.filemanagecentre.dto.FilesDto;
import com.neco.filemanagecentre.model.CentreFileInfo;
import com.neco.sglog.annotation.SgLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author ziyuan_deng
 * @date 2020/9/28
 */
@Api(value = "OpenFileController", description = "文件对外开放操作接口")
@RequestMapping("/openfile")
@RestController
@Slf4j
public class OpenFileController {

    private final long downloadFileSize = 5*1024;

    @Autowired
    private FileService fileService;

    @Autowired
    private AppendFileStorageClient fileStorageClient;

    @Autowired
    private FastFileStorageClient storageClient;


    @ApiOperation(value = "资源文件中心", notes = "文件查询列表")
    @PostMapping("/list")
    public ModelMap list(@RequestBody FilesDto filesDto){
        if (filesDto == null) {
            filesDto = new FilesDto();
            filesDto.setPageNum(0);
            filesDto.setPageSize(10);
            filesDto.setPublicStatus(1);
        }else{
            filesDto.setPageNum(filesDto.getPageNum()==null ? 0 : filesDto.getPageNum());
            filesDto.setPageSize(filesDto.getPageSize()==null ? 10 : filesDto.getPageSize());
            filesDto.setPublicStatus(1);
        }
        return ResponseUtil.retCorrectModel(fileService.selectFilesByPage(filesDto));
    }

    /**
     * fastDFS单个文件下载的方法
     * @param request
     * @param response
     * @param id
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
                return  ResponseUtil.retCorrectInfo("下载成功");
            }
            byte[] bytes = storageClient.downloadFile(groupName, remoteFileName,new DownloadByteArray());
            out.write(bytes);
            out.flush();
            boolean flag = updateDownloadCount(files);
            log.info("下载成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            out.close();
        }
        return ResponseUtil.retCorrectInfo("下载成功");
    }

    /**
     * fastdfs批量下载的方法
     *
     * @param ids
     * @return
     */
    @ApiOperation(value = "批量文件下载")
    @SgLog(module = "资源文件中心",description = "对外批量文件下载")
    @GetMapping("/batchDownloadFiles")
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
            log.error("文件压缩失败！",e);
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
}
