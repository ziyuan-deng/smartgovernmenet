package com.neco.filemanage.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.neco.filemanage.mapper.FileMapper;
import com.neco.filemanage.service.FileService;
import com.neco.filemanagecentre.dto.FilesDto;
import com.neco.filemanagecentre.model.CentreFileInfo;
import com.neco.filemanagecentre.model.FileAssignInfo;
import com.neco.filemanagecentre.model.Files;
import com.neco.utils.ReflectUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件业务处理类
 *
 * @author ziyuan_deng
 * @create 2020-09-06 20:59
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, CentreFileInfo> implements FileService {
    @Autowired
    private FileMapper fileMapper;



    @Override
    public PageInfo<CentreFileInfo> selectFilesByPage(FilesDto filesDto) {
       // Map<String, Object> conditionMap = ReflectUtil.beanToMap(filesDto, true);
        PageHelper.startPage(filesDto.getPageNum(),filesDto.getPageSize());
        Wrapper<CentreFileInfo> wrapper = new EntityWrapper<>();
        if (filesDto.getPublicStatus()!=null){
            wrapper.eq("public_status",filesDto.getPublicStatus());
        }
        if (StringUtils.isNotBlank(filesDto.getAppId())) {
            wrapper.eq("app_id",filesDto.getAppId());
        }
        if (StringUtils.isNotBlank(filesDto.getCompanyId())) {
            wrapper.eq("company_id",filesDto.getCompanyId());
        }
        if (StringUtils.isNotBlank(filesDto.getFileName())) {
            wrapper.like("file_name",filesDto.getFileName());
        }
        wrapper.orderBy("create_time",true);
        List<CentreFileInfo> list = fileMapper.selectList(wrapper);
        return new PageInfo<>(list);
    }

    @Override
    public int replaceFile(MultipartFile file, CentreFileInfo centreFileInfo) {
        return 0;
    }

}
