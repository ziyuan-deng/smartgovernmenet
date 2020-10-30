package com.neco.filemanagecentre.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.Serializable;

/**
 * 大文件信息
 * @author ziyuan_deng
 * @date 2020/10/9
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BigFiles extends CentreFileInfo implements Serializable {

    /**
     * 当前块的次序，第一个块是 1，注意不是从 0 开始的。
     */
    private Integer chunkNumber;
    /**
     * 文件被分成块的总数。
     */
    private Integer totalChunks;
    /**
     * 分块大小，根据 totalSize 和这个值你就可以计算出总共的块数。注意最后一块的大小可能会比这个要大。
     */
    private Long chunkSize;
    /**
     * 当前块的大小，实际大小。
     */
    private Long currentChunkSize;
    /**
     * 文件总大小。
     */
    private Long totalSize;
    /**
     * 这个就是每个文件的唯一标示
     */
    private String identifier;
    /**
     * 文件名。
     */
    private String filename;
    /**
     * 文件夹上传的时候文件的相对路径属性
     */
    private String relativePath;

    /**
     * 临时文件目录
     */
    private File tempPath;

}
