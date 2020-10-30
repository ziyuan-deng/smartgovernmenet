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
 * 系统文件分配额度实体
 * @author ziyuan_deng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableName("t_filecentre_fileassigninfo")
public class FileAssignInfo {

    @TableId(value="id",type = IdType.UUID)
    private String id;

    //所在单位id
    @TableField(value="company_id")
    private String companyId;

    //所在单位名称
    @TableField(value="company_name")
    private String companyName;

    //应用id
    @TableField(value="app_id")
    private String appId;

    //应用名称
    @TableField(value="app_name")
    private String appName;

    //分配额度
    @TableField(value="assign_size")
    private Long assignSize;

    //已使用配额
    @TableField(value="used_size")
    private Long usedSize;

    //默认保存天数
    @TableField(value="defaul_save_date")
    private Integer defaulSaveDate;

    //上传状态：0表示不允许上传，1表示可以上传
    @TableField(value="upload_status")
    private Integer uploadStatus;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(value="create_time")
    private Date createTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(value="update_time")
    private Date updateTime;

}