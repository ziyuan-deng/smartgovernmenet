package com.neco.filemanagecentre.dto;

import com.neco.filemanagecentre.model.CentreFileInfo;
import lombok.*;

import java.util.Arrays;
import java.util.Date;

/**
 * @author ziyuan_deng
 * @date 2020/9/7
 */
@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class FilesDto extends CentreFileInfo {



    private String folderIdList;
    /**
     * 缩略图URL
     */
    private String thumbnailUrl;
    /**
     * 文件用途：1：图论价值观
     */
    private String useType;

    private Integer pageNum;

    private Integer pageSize;

    /**
     * 文件byte数组
     */
    private byte[] content;

    public FilesDto(byte[] content,String postfix){
        this.content = content;
        super.setPostfix(postfix);
    }


}
