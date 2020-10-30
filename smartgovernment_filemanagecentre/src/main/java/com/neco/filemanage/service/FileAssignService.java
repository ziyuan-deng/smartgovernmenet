package com.neco.filemanage.service;

import com.baomidou.mybatisplus.service.IService;
import com.github.pagehelper.PageInfo;
import com.neco.filemanagecentre.dto.FileAssignInfoDto;
import com.neco.filemanagecentre.model.FileAssignInfo;
import org.springframework.ui.ModelMap;

public interface FileAssignService extends IService<FileAssignInfo> {

    PageInfo<FileAssignInfo> selectParamDataByPage(FileAssignInfoDto assignInfoDto);

    ModelMap insertAssignInfo(FileAssignInfoDto assignInfoDto);

    FileAssignInfo selectByCompanyIdOrAppId(String companyId, String appId);
}
