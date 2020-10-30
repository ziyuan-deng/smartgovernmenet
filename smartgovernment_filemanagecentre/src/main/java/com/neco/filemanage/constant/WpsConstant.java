package com.neco.filemanage.constant;

import java.util.Arrays;
import java.util.List;

/**
 * WPS有关常量定义
 * @author ziyuan_deng
 * @date 2020/10/13
 */
public class WpsConstant {

    // 权限 - 预览
    public static final String PERMISSION_READ = "read";
    // 权限 - 可编辑
    public static final String PERMISSION_WRITE = "write";

    // 文件类型 - w Word类型文件
    public static final List<String> W_TYPE_LIST = Arrays.asList("doc", "dot", "wps", "wpt", "docx", "dotx", "docm", "dotm", "rtf");
    // 文件类型 - s excel类型文件
    public static final List<String> S_TYPE_LIST = Arrays.asList("xls", "xlt", "et", "xlsx", "xltx", "csv", "xlsm", "xltm");
    // 文件类型 - p PPT幻灯片类型文件
    public static final List<String> P_TYPE_LIST = Arrays.asList("ppt","pptx","pptm","ppsx","ppsm","pps","potx","potm","dpt","dps");
    // 文件类型 - f PDF文件
    public static final List<String> F_TYPE_LIST = Arrays.asList("pdf");
    // 新的文档标识
    public static final String NEW_ATTACH_ID = "new";

    // 文件编号在请求头中的字段名
    public static final String WEBOFFICE_FILE_ID_NAME = "x-weboffice-file-id";


}
