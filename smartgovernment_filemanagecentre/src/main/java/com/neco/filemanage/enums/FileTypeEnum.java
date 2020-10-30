package com.neco.filemanage.enums;

/**
 * fastDSF文件类型枚举
 * @author ziyuan_deng
 * @create 2020-09-06 21:18
 */
public enum FileTypeEnum {

    PHOTO("图片", "0"),
    DOCUMENT("文档", "1"),
    TABLE("表格", "2"),
    VIDEO("视频", "3"),
    NONE("其他", "4");

    // 成员变量
    private String msg;
    private String code;

    // 构造方法
    private FileTypeEnum(String msg, String code) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
