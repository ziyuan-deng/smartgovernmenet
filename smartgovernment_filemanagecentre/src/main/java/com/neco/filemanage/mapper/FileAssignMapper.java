package com.neco.filemanage.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.neco.filemanagecentre.model.FileAssignInfo;

import java.util.List;
import java.util.Map;

public interface FileAssignMapper extends BaseMapper<FileAssignInfo> {

    List<FileAssignInfo> selectParamData(Map<String, Object> conditionMap);
}
