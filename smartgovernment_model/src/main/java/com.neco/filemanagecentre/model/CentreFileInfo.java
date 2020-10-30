package com.neco.filemanagecentre.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Date;

/**
 * 上传/下载文件实体类
 * @author ziyuan_deng
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@TableName("t_filecentre_file")
public class CentreFileInfo {

    @TableId(value="id",type = IdType.UUID)
    private String id;
    //文件夹id
    @TableField(value="folder_id")
    private String folderId;

    //上传人所在单位id
    @TableField(value="company_id")
    private String companyId;

    //上传人所在单位名称
    @TableField(value="company_name")
    private String companyName;

    //上传人所在部门ID
    @TableField(value="department_id")
    private String departmentId;

    //上传人所在部门名称
    @TableField(value="department_name")
    private String departmentName;

    //第三方应用标识符
    @TableField(value="app_id")
    private String appId;

    //文件后缀名称
    @TableField(value="postfix")
    private String postfix;

    //文件类型:0 图片 1文档 2表格 3视频 4其他
    @TableField(value="file_type")
    private Integer fileType;

    //文件mime类型
    @TableField(value="mime_type")
    private String mimeType;

    //文件大小
    @TableField(value="file_size")
    private Long fileSize;

    //访问次数
    @TableField(value="visit_count")
    private Integer visitCount;

    //下载次数
    @TableField(value="download_count")
    private Integer downloadCount;

    //文件rul
    @TableField(value="url")
    private String url;

    //源文件名
    @TableField(value="file_name")
    private String fileName;

    //上传文件系统的组名
    @TableField(value="group_name")
    private String groupName;

    //磁盘名与目录名
    @TableField(value="remote_filename")
    private String remoteFileName;

    //上传用户id
    @TableField(value="upload_user_id")
    private String uploadUserId;

    //上传用户名字
    @TableField(value="upload_user_name")
    private String uploadUserName;

    //上传文件的公开状态：0表示不公开，1表示公开
    @TableField(value="public_status")
    private Integer publicStatus;

    //创建时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(value="create_time")
    private Date createTime;

    //更新时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(value="update_time")
    private Date updateTime;

    //文件版本号
    @TableField(value="version")
    private Integer version;

    //源文件ID
    @TableField(value="version")
    private String sourceFileId;

    /**
     * 文件byte数组
     */
 /*   private byte[] content;

    public CentreFileInfo(byte[] content,String postfix){
        this.content = content;
        this.postfix = postfix;
    }*/


}