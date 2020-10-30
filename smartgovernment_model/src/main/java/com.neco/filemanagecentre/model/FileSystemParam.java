package com.neco.filemanagecentre.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * 文件系统上传文件类型白名单与上传文件的大小限制参数实体
 * @author ziyuan_deng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableName("t_filecentre_filesystemparam")
public class FileSystemParam {


    @TableId(value="id",type = IdType.UUID)
    private String id;

    //系统支持的文件类型，每个类型逗号隔开
    @TableField(value="permit_types")
    private String permitTypes;

    //系统允许上传文件大小
    @TableField(value="file_size")
    private Long fileSize;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(value="create_time")
    private Date createTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(value="update_time")
    private Date updateTime;
}