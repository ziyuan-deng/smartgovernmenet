package com.neco.filemanage.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.neco.common.response.ResponseUtil;
import com.neco.filemanage.mapper.FileAssignMapper;
import com.neco.filemanage.service.FileAssignService;
import com.neco.filemanagecentre.dto.FileAssignInfoDto;
import com.neco.filemanagecentre.model.FileAssignInfo;
import com.neco.utils.ReflectUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.Map;

/**
 * @author ziyuan_deng
 * @date 2020/9/25
 */
@Service
public class FileAssignServiceImpl extends ServiceImpl<FileAssignMapper, FileAssignInfo> implements FileAssignService {

    @Autowired
    private FileAssignMapper assignMapper;

    /**
     * 分页查询获取数据项
     * @param assignInfoDto
     * @return
     */
    @Override
    public PageInfo<FileAssignInfo> selectParamDataByPage(FileAssignInfoDto assignInfoDto) {

        Map<String, Object> conditionMap = ReflectUtil.beanToMap(assignInfoDto, true);
        //分页处理
        PageHelper.startPage(assignInfoDto.getPageNum(),assignInfoDto.getPageSize());
        List<FileAssignInfo> list = assignMapper.selectParamData(conditionMap);
        return new PageInfo<>(list);
    }

    /**
     * 保存数据
     * @param assignInfoDto
     * @return
     */
    @Override
    public ModelMap insertAssignInfo(FileAssignInfoDto assignInfoDto) {
        Wrapper<FileAssignInfo> wrapper = new EntityWrapper<>();
        if (StringUtils.isNotBlank(assignInfoDto.getCompanyId())) {
            wrapper.eq("company_id",assignInfoDto.getCompanyId());
        }
        if (StringUtils.isNotBlank(assignInfoDto.getAppId())) {
            wrapper.eq("app_id",assignInfoDto.getAppId());
        }
        int count = this.selectCount(wrapper);
        if (count>0) {
            return ResponseUtil.retErrorInfo("单位或者应用已经分配，请确认！");
        }
        boolean flag = insert(assignInfoDto);
        if (!flag) {
            return ResponseUtil.retErrorInfo("数据保存失败！");
        }
        return ResponseUtil.retCorrectModel(flag);
    }

    @Override
    public FileAssignInfo selectByCompanyIdOrAppId(String companyId, String appId) {
        Wrapper<FileAssignInfo> wrapper = new EntityWrapper<>();
        if (StringUtils.isNotBlank(appId)) {
            wrapper.eq("app_id",appId);
        }else {
            if (StringUtils.isNotBlank(companyId)) {
                wrapper.eq("company_id",companyId);
            }
        }
        List<FileAssignInfo> fileAssignInfos = this.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(fileAssignInfos)) {
            FileAssignInfo assignInfo = fileAssignInfos.get(0);
            return assignInfo;
        }
        return null;

    }

}
