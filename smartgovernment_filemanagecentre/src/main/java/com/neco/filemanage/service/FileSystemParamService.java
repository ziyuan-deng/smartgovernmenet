package com.neco.filemanage.service;

import com.baomidou.mybatisplus.service.IService;
import com.neco.filemanagecentre.dto.FileSystemParamDto;
import com.neco.filemanagecentre.model.FileSystemParam;
import com.neco.filemanagecentre.vo.FileSystemParamVo;

import java.util.List;

public interface FileSystemParamService extends IService<FileSystemParam> {

    FileSystemParamVo selectSystemParamList();

}
