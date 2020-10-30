package com.neco.filemanagecentre.dto;

import com.neco.filemanagecentre.model.FileAssignInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author ziyuan_deng
 * @date 2020/9/25
 */
@Data
@NoArgsConstructor
@ToString
public class FileAssignInfoDto extends FileAssignInfo {

    private Integer pageNum;

    private Integer pageSize;
}
