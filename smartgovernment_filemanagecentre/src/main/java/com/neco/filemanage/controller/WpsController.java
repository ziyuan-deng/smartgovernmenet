package com.neco.filemanage.controller;

import com.neco.common.response.ResponseUtil;
import com.neco.common.web.BaseController;
import com.neco.filemanage.constant.FileConstant;
import com.neco.filemanage.constant.WpsConstant;
import com.neco.filemanage.service.FileService;
import com.neco.filemanage.wps.WpsRequestParameter;
import com.neco.filemanagecentre.model.CentreFileInfo;
import com.sun.xml.internal.ws.api.message.Attachment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文档在线编辑接口
 *
 * @author ziyuan_deng
 * @date 2020/10/13
 */
@RestController
@RequestMapping("/wps")
@Slf4j
public class WpsController extends BaseController {

    @Value("${config.wps.app-id:6a87dc2c0fe6441b8b0fbae683c8f928}")
    private String wpsAppId;
    @Value("${config.wps.app-key:006babf9b28c49459941f298bb3df995}")
    private String wpsAppKey;
    @Value("${config.wps.service-url:https://wwo.wps.cn/office}")
    private String wpsServiceUrl;
    @Autowired
    private FileService fileService;


    /**
     * 获取查看文档的URL
     * @return
     */
    @GetMapping("/serviceUrl/{fileId}")
    public ModelMap getViewUrl(@PathVariable String fileId, String mode){
        if(!WpsConstant.PERMISSION_READ.equals(mode) && !WpsConstant.PERMISSION_WRITE.equals(mode))
            mode = WpsConstant.PERMISSION_READ;
        CentreFileInfo fileInfo = fileService.selectById(fileId);
        //Assert.notNull(attachment, "查看的文档不存在");
        if (fileInfo == null) {
            ResponseUtil.retErrorInfo("查看的文档不存在");
        }
        WpsRequestParameter p = WpsRequestParameter.create()
                .addWps(FileConstant.URL_TOKEN, getLoginUserToken())
                .addWps("mode", mode)
                .addWps("appid", wpsAppId);
        StringBuffer sb = new StringBuffer(512);
        sb.append(wpsServiceUrl).append("/").append(getWpsFileType(fileInfo.getPostfix()))
                .append("/").append(fileId).append("?")
                .append(p.signatureUrl(wpsAppKey));
       // return successResult("获取文档预览地址成功", sb.toString());
        return ResponseUtil.retCorrectModel(sb.toString(),"获取文档预览地址成功");
    }



    /**
     * 获取指定后缀名对应的 WPS 类型
     * @param e 后缀名
     * @return
     */
    private String getWpsFileType(String e){
        Assert.hasText(e, "未知文件类型，无法进行在线查看");
        e = e.toLowerCase();
        String t = null;
        if(WpsConstant.W_TYPE_LIST.contains(e))
            t = "w";
        else if(WpsConstant.F_TYPE_LIST.contains(e))
            t = "f";
        else if(WpsConstant.S_TYPE_LIST.contains(e))
            t = "s";
        else if(WpsConstant.P_TYPE_LIST.contains(e))
            t = "p";
        else
            Assert.isTrue(false, "不支持的文件类型");
        return t;
    }

}
