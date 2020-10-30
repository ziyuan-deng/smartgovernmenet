package com.neco.filemanage.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.neco.filemanage.mapper.FileSystemParamMapper;
import com.neco.filemanage.service.FileSystemParamService;
import com.neco.filemanagecentre.dto.FileSystemParamDto;
import com.neco.filemanagecentre.model.FileSystemParam;
import com.neco.filemanagecentre.vo.FileSystemParamVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author ziyuan_deng
 * @date 2020/9/25
 */
@Service
public class FileSystemParamServiceImpl extends ServiceImpl<FileSystemParamMapper, FileSystemParam> implements FileSystemParamService {

    /**
     * 查询系统大小设置和文件类型白名单
     * @return
     */
    @Override
    public FileSystemParamVo selectSystemParamList() {
        List<FileSystemParam> fileSystemParams = this.selectList(null);
        FileSystemParamVo paramVo = new FileSystemParamVo();
        if (CollectionUtils.isNotEmpty(fileSystemParams)) {
            FileSystemParam fileSystemParam = fileSystemParams.get(0);
            BeanUtils.copyProperties(fileSystemParam,paramVo);
            String permitTypes = fileSystemParam.getPermitTypes();
            /*Map typeMap = JSON.parseObject(permitTypes, Map.class);
            paramVo.setFileTypeMap(typeMap);*/
            String[] typeArr = permitTypes.split(",");
            paramVo.setTypeList(Arrays.asList(typeArr));
            return paramVo;
        }
        return null;
    }


}
