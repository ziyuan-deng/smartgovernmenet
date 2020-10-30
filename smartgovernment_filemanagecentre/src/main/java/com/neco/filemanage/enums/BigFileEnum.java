package com.neco.filemanage.enums;

/**
 * @author ziyuan_deng
 *
 */
public enum BigFileEnum {
    /**
     * 保存Files对象key前缀
     */
    BIG_FILE("BIG_FILE_"),
    /**
     * 保存追加文件到第几个块的key前缀
     */
    BIG_FILE_NUMBER("BIG_FILE_NUMBER_"),
    /**
     * 保存所保存区块的key前缀
     */
    BIG_FILE_TOTAL("BIG_FILE_TOTAL_"),
    /**
     * 保存文件MD5
     */
    BIG_FILE_MD5("BIG_FILE_MD5_")
    ;


    private String value;

    private BigFileEnum(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
