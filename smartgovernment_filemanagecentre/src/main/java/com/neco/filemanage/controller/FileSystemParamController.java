package com.neco.filemanage.controller;

import com.neco.common.response.ResponseUtil;
import com.neco.filemanage.service.FileSystemParamService;
import com.neco.filemanagecentre.dto.FileSystemParamDto;
import com.neco.utils.ComUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author ziyuan_deng
 * @date 2020/9/25
 */
@Api(value = "FileSystemParamController", description = "文件系统白名单与上传量控制综合接口")
@RequestMapping("/fileParam")
@RestController
@Slf4j
public class FileSystemParamController {

    @Autowired
    private FileSystemParamService paramService;

    /**
     * 获取文件系统设置信息
     * @return
     */
    @GetMapping("/list")
    public ModelMap list(){
        return ResponseUtil.retCorrectModel(paramService.selectSystemParamList());
    }

    @PostMapping("/save")
    public ModelMap saveParamData(@RequestBody FileSystemParamDto paramDto){
        int count = paramService.selectCount(null);
        if (count>0){
            return ResponseUtil.retErrorInfo("数据已经存在，不能保存多份！");
        }
        if (paramDto==null) {
            return ResponseUtil.retErrorInfo("上传数据为空！");
        }
        paramDto.setId(ComUtil.randomUUID());
        paramDto.setCreateTime(new Date());
        paramDto.setUpdateTime(new Date());
        return ResponseUtil.retCorrectModel(paramService.insert(paramDto));
    }

    @PostMapping("/update")
    public ModelMap updateParamData(@RequestBody FileSystemParamDto paramDto){
        if (paramDto==null) {
            return ResponseUtil.retErrorInfo("上传数据为空！");
        }
        boolean flag = paramService.updateById(paramDto);
        if (flag) {
            return ResponseUtil.retCorrectModel(flag,"保存数据成功！");
        }else{
            return  ResponseUtil.retErrorInfo("保存数据失败！");
        }
    }

    /**
     * 删除数据
     * @param id
     * @return
     */
    @DeleteMapping("/delete")
    public ModelMap deleteParamData(String id){
        if (StringUtils.isBlank(id)) {
            return ResponseUtil.retErrorInfo("请求参数为空！");
        }
        boolean flag = paramService.deleteById(id);
        if (flag) {
            return ResponseUtil.retCorrectModel(flag,"删除数据成功！");
        }else{
            return ResponseUtil.retCorrectModel(flag,"删除数据成功！");
        }
    }


}
