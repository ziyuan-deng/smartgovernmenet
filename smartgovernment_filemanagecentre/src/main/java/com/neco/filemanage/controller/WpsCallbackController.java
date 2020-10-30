package com.neco.filemanage.controller;

import com.alibaba.fastjson.JSONObject;
import com.neco.common.response.ResponseUtil;
import com.neco.common.web.BaseController;
import com.neco.common.web.SgUser;
import com.neco.filemanage.constant.WpsConstant;
import com.neco.filemanage.service.FileService;
import com.neco.filemanage.wps.WpsRequestParameter;
import com.neco.filemanagecentre.model.CentreFileInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * wps回调接口
 * @author ziyuan_deng
 * @date 2020/10/13
 */
@RestController
@RequestMapping("/v1/3rd/file")
@Slf4j
public class WpsCallbackController extends  BaseController {

    @Autowired
    private FileService fileService;

    /**
     * 获取文件元数据
     */
    @GetMapping("/info")
    public ModelMap getFileInfo(HttpServletRequest request){
        WpsRequestParameter param = WpsRequestParameter.create(request);
        try {
            // 获取基本信息
            String fileId = request.getHeader(WpsConstant.WEBOFFICE_FILE_ID_NAME), token = param.getWps("token"), mode = param.getWps("mode");
            SgUser user = getCurrUser(request);
            CentreFileInfo centreFileInfo = fileService.selectById(fileId);
            if (centreFileInfo == null) {
                ResponseUtil.retErrorInfo("文档不存在!");
            }
            JSONObject fileInfo = buildFileMetaInfo(user, centreFileInfo, mode);
            return ResponseUtil.retCorrectModel(fileInfo);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseUtil.retErrorInfo("获取文件元数据异常");
        }

    }

    /**
     * 保存新的版本
     */
    @PostMapping("save")
    public ModelMap fileSave(@RequestBody MultipartFile file, HttpServletRequest request) {
        WpsRequestParameter param = WpsRequestParameter.create(request);

        try {
            // 获取基本信息
            String fileId = request.getHeader(WpsConstant.WEBOFFICE_FILE_ID_NAME), token = param.getWps("token");
            SgUser user = getCurrUser(request);
            CentreFileInfo centreFileInfo = fileService.selectById(fileId);
            if (centreFileInfo == null) {
                ResponseUtil.retErrorInfo("文档不存在!");
            }
            JSONObject fileInfo = buildSaveInfo(user, centreFileInfo);
            fileService.replaceFile(file, centreFileInfo);
            return ResponseUtil.retCorrectModel(fileInfo);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseUtil.retErrorInfo("获取文件元数据异常");
        }
    }

    /**
     * 创建保存文件信息的对象
     * @param user 当前登录用户  todo
     * @param centreFileInfo 文件信息实体对象
     * @return
     */
    private JSONObject buildSaveInfo(SgUser user, CentreFileInfo centreFileInfo) {
        JSONObject result = new JSONObject(), file = new JSONObject();
        result.put("file", file);
        // 文件信息
        file.put("id", centreFileInfo.getId()); // 文件id，字符串长度小于40
        file.put("name", centreFileInfo.getFileName()); // 文件名
        file.put("version", 1); // 当前版本号，位数小于11
        file.put("size", centreFileInfo.getFileSize()); // 文件大小
        file.put("download_url",centreFileInfo.getUrl());   // 文件下载地址
        return result;
    }

    private JSONObject buildFileMetaInfo(SgUser user, CentreFileInfo centreFileInfo, String mode) {

        JSONObject result = new JSONObject(), file = new JSONObject(), userObj = new JSONObject(), acl = new JSONObject(), watermark = new JSONObject();
        File oFile = new File(centreFileInfo.getUrl());
        result.put("file", file);
        result.put("user", userObj);
        if(!oFile.exists())
            return result;
        // 构建文件对象
        file.put("id", centreFileInfo.getId()); // 文件编号
        file.put("name", centreFileInfo.getFileName()); // 文件名
        file.put("version", 1); // 版本号
        file.put("size", centreFileInfo.getFileSize()); // 文件大小
        file.put("creator", centreFileInfo.getUploadUserId()); // 创建人
        file.put("create_time", centreFileInfo.getCreateTime().getTime()/1000); // 创建时间，时间戳，单位为秒
        file.put("modifier", centreFileInfo.getUploadUserId());   // 修改者
        file.put("modify_time", centreFileInfo.getUpdateTime().getTime()/1000); // 修改时间，时间戳，单位为秒
        file.put("download_url", centreFileInfo.getUrl());   // 文档下载地址
//        file.put("download_url", downloadUrlPrefix + "3654DC4D97B144CFB30D5064BC5A4401" );   // 文档下载地址
        file.put("preview_pages", 999); // 限制预览页数
        // 文件访问控制
        acl.put("rename", 0);   // 重命名权限，1为打开该权限，0为关闭该权限，默认为0
        acl.put("history", 1);  // 历史版本权限，1为打开该权限，0为关闭该权限,默认为1
        acl.put("copy", 1); // 复制
        acl.put("export", 1);   // 导出PDF
        acl.put("print", 1);    // 打印
        file.put("user_acl", acl);
        // 水印
       // SysUser sysUser = ((SysLoginUser)loginUser).getSysUser();
        // 用户存在时使用水印
        /*if(sysUser != null){
            watermark.put("type", 1);
            StringBuilder waterText = new StringBuilder(64);
            waterText.append(sysUser.getRealName()).append("　");
            String mobile = sysUser.getMobilePhone();
            if(StringUtils.hasText(mobile) && mobile.length() >= 11){
                waterText.append(mobile.substring(7));
            }else{
                waterText.append("0000");
            }
            waterText.append("　OACard");
            watermark.put("value", waterText.toString());
            watermark.put("font", "bold 24px Serif");
            watermark.put("fillstyle", "rgba(192, 192, 192, 0.28)");
            watermark.put("rotate", -0.7853982);
        }else{
            watermark.put("type", 0);   // 不使用水印
        }

        file.put("watermark", watermark);
        // 当前用户
        user.put("id", loginUser.getId());  // 当前用户
        user.put("name", loginUser.getRealName());  // 用户名称
        user.put("permission", mode);   // 用户操作权限，write：可编辑，read：预览
        user.put("avatar_url", ((SysLoginUser)loginUser).getSysUser().getAvatar()); // 用户头像地址
        */
        return result;
    }

}
