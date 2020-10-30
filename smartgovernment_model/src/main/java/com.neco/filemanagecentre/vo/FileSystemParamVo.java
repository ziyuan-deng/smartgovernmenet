package com.neco.filemanagecentre.vo;

import com.neco.filemanagecentre.model.FileSystemParam;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * @author ziyuan_deng
 * @date 2020/9/25
 */
@Data
@NoArgsConstructor
@ToString
public class FileSystemParamVo extends FileSystemParam {

    Map<String, List<String>> fileTypeMap;

    List<String> typeList;
}
