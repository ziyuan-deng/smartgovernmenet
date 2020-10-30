package com.neco.filemanage.controller;

import com.neco.common.response.ResponseUtil;
import com.neco.filemanage.service.FileAssignService;
import com.neco.filemanagecentre.dto.FileAssignInfoDto;
import com.neco.utils.ComUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author ziyuan_deng
 * @date 2020/9/25
 */
@Api(value = "FileAssignController", description = "系统文件分配额度")
@RequestMapping("/fileAssign")
@RestController
@Slf4j
public class FileAssignController {

    @Autowired
    private FileAssignService assignService;

    /**
     * 分页查询文件分配额度项
     * @param assignInfoDto
     * @return
     */
    @PostMapping("/list")
    public ModelMap list(@RequestBody  FileAssignInfoDto assignInfoDto){
        if (assignInfoDto == null) {
            assignInfoDto = new FileAssignInfoDto();
            assignInfoDto.setPageNum(0);
            assignInfoDto.setPageSize(10);
        }else{
            assignInfoDto.setPageNum(assignInfoDto.getPageNum()==null ? 0 : assignInfoDto.getPageNum());
            assignInfoDto.setPageSize(assignInfoDto.getPageSize()==null ? 10 : assignInfoDto.getPageSize());
        }
        return ResponseUtil.retCorrectModel(assignService.selectParamDataByPage(assignInfoDto));
    }

    /**
     *保存分配数据
     * @param assignInfoDto
     * @return
     */
    @PostMapping("/save")
    public ModelMap saveAssignInfo(@RequestBody  FileAssignInfoDto assignInfoDto){
        if (assignInfoDto == null) {
            return  ResponseUtil.retErrorInfo("请求参数为空！");
        }
        if (StringUtils.isBlank(assignInfoDto.getCompanyId()) && StringUtils.isBlank(assignInfoDto.getAppId())) {
            return  ResponseUtil.retErrorInfo("分配给的单位或第三方应用不能为空！");
        }
        assignInfoDto.setId(ComUtil.randomUUID());
        assignInfoDto.setCreateTime(new Date());
        assignInfoDto.setUpdateTime(new Date());
        return assignService.insertAssignInfo(assignInfoDto);
    }

    /**
     * 根据ID获取对应的分配额度信息
     * @param id
     * @return
     */
    @PostMapping("/getOne")
    public ModelMap getAssignInfo(String id){
        if (StringUtils.isBlank(id)) {
            return  ResponseUtil.retErrorInfo("请求参数为空！");
        }
        return ResponseUtil.retCorrectModel(assignService.selectById(id));
    }

    /**
     *更新分配数据
     * @param assignInfoDto
     * @return
     */
    @PostMapping("/update")
    public ModelMap updateAssignInfo(@RequestBody  FileAssignInfoDto assignInfoDto){
        if (assignInfoDto == null) {
            return  ResponseUtil.retErrorInfo("请求参数为空！");
        }
        if (StringUtils.isBlank(assignInfoDto.getCompanyId()) || StringUtils.isBlank(assignInfoDto.getAppId())) {
            return  ResponseUtil.retErrorInfo("分配给的单位或第三方应用不能为空！");
        }
        assignInfoDto.setUpdateTime(new Date());
        boolean flag = assignService.updateById(assignInfoDto);
        if (!flag) {
            return  ResponseUtil.retErrorInfo("更新数据失败！");
        }
        return ResponseUtil.retCorrectModel(flag);
    }

    /**
     * 根据ID删除分配数据
     * @param ids
     * @return
     */
    @DeleteMapping("/delete")
    public ModelMap deleteAssignInfo(String ids){
        if (StringUtils.isBlank(ids)) {
            return  ResponseUtil.retErrorInfo("请求参数为空！");
        }
        String[] idArr = ids.split(",");
        List<String> idList = Arrays.asList(idArr);
        boolean flag = assignService.deleteBatchIds(idList);
        if (!flag) {
            return  ResponseUtil.retErrorInfo("删除数据失败！");
        }
        return ResponseUtil.retCorrectModel(flag);
    }


}
