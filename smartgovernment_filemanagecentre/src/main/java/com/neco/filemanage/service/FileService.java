package com.neco.filemanage.service;

import com.baomidou.mybatisplus.service.IService;
import com.github.pagehelper.PageInfo;
import com.neco.filemanagecentre.dto.FilesDto;
import com.neco.filemanagecentre.model.CentreFileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService extends IService<CentreFileInfo> {

    PageInfo<CentreFileInfo> selectFilesByPage(FilesDto filesDto);

    int replaceFile(MultipartFile file, CentreFileInfo centreFileInfo);
}
