package com.neco.filemanagecentre.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件信息
 * @author ziyuan_deng
 * @create 2020-09-05 17:24
 */
@TableName("t_filecentre_file")
public class Files implements Serializable {

    @TableId(value="id",type = IdType.UUID)
    private String id;

    @TableField(value="folder_id")
    private String folderId;

    @TableField(value="file_name")
    private String fileName;

    /**
     * 后缀名
     */
    @TableField(value="file_name")
    private String postfix;

    @TableField(value="group_name")
    private String groupName;

    @TableField(value="remote_filename")
    private String remoteFileName;

    /**
     * 文件类型：0 图片 1文档 2表格
     */
    @TableField(value="types")
    private String type;

    @TableField(value="url")
    private String url;

    @TableField(value="create_user")
    private String createUser;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(value="create_time")
    private Date createTime;


    public Files() {
    }

    public Files(String id, String fileName, String folderId, String type, String url, Date createTime, String createUser) {
        this.id = id;
        this.fileName = fileName;
        this.folderId = folderId;
        this.type = type;
        this.url = url;
        this.createTime = createTime;
        this.createUser = createUser;
    }


    public Files(String id, String fileName, String url){
        this.id = id;
        this.fileName = fileName;
        this.url = url;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getRemoteFileName() {
        return remoteFileName;
    }

    public void setRemoteFileName(String remoteFileName) {
        this.remoteFileName = remoteFileName;
    }

    public String getPostfix() {
        return postfix;
    }

    public void setPostfix(String postfix) {
        this.postfix = postfix;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }


    @Override
    public String toString() {
        return "Files{" +
                "id='" + id + '\'' +
                ", folderId='" + folderId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", postfix='" + postfix + '\'' +
                ", groupName='" + groupName + '\'' +
                ", remoteFileName='" + remoteFileName + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", createUser='" + createUser + '\'' +
                ", createTime=" + createTime +
                '}';
    }


}
