package com.neco.messagecentre.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ziyuan_deng
 * @date 2020/9/17
 */
@TableName("t_message_infomation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageInfo implements Serializable {

    @TableId(value="id",type = IdType.UUID)
    private String id;

    @TableField(value="content")
    private String content;

    @TableField(value="status")
    private String status;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(value="create_time")
    private Date  createTime;

}
