package com.neco.filemanage.utils;


import com.alibaba.fastjson.JSON;
import com.neco.common.enums.CodeEnum;
import com.neco.filemanage.enums.FileTypeEnum;
import com.neco.filemanagecentre.model.Files;

import java.util.*;

/**
 * @author ziyuan_deng
 * @create 2020-09-06 21:27
 */
public class FileType {

    public static String fileType(String fileName) {
        if (fileName == null) {
            return CodeEnum.PARAMS_ERROR.getMsg();

        } else {
            // 获取文件后缀名并转化为小写，用于后续比较
            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            // 创建图片类型数组0
            String[] img = { "bmp", "jpg", "jpeg", "png", "gif"};
            for (int i = 0; i < img.length; i++) {
                if (img[i].equals(fileType)) {
                    return FileTypeEnum.PHOTO.getCode();
                }
            }

            // 创建文档类型数组1
            String[] document = { "txt", "doc", "docx", "html", "jsp", "pdf", "ppt","docm","dotx","dotm" };
            for (int i = 0; i < document.length; i++) {
                if (document[i].equals(fileType)) {
                    return FileTypeEnum.DOCUMENT.getCode();
                }
            }
            // 创建表格类型2
            String[] table = { "xls","xlsx","xlsm","xltx","xltm","xlsb","xlam" };
            for (int i = 0; i < table.length; i++) {
                if (table[i].equals(fileType)) {
                    return FileTypeEnum.TABLE.getCode();
                }
            }
            // 创建音视频类型3
            String[] video = { "mov","avi","rmvb","rm","flv","mp4","3GP" };
            for (int i = 0; i < table.length; i++) {
                if (table[i].equalsIgnoreCase(fileType)) {
                    return FileTypeEnum.VIDEO.getCode();
                }
            }
            //其他类型4
            if(fileType != null){
                return FileTypeEnum.NONE.getCode();
            }

        }
        return CodeEnum.UNKNOWN_ERROR.getMsg();
    }
    public static Files getFileTypesName(Files files){
        String type = files.getType();
        String str = "";
        for (FileTypeEnum fileTypeEnum : FileTypeEnum.values()){
            if(fileTypeEnum.getCode().equals(type)){
                str = fileTypeEnum.getMsg();
            }
        }
        if ("".equals(str)) {
            str = FileTypeEnum.NONE.getMsg();
        }
        files.setType(str);
        return files;
    }

    public static void main(String[] args) {
        Map<String, List<String>> dataMap = new HashMap<>();
        String[] img = { "bmp", "jpg", "jpeg", "png", "gif"};
        List<String> imgList = new ArrayList<>();
        imgList.addAll(new ArrayList<String>(Arrays.asList(img)));
        dataMap.put("img",imgList);

        String[] document = { "txt", "doc", "docx", "html", "jsp", "pdf", "ppt","docm","dotx","dotm" };
        List<String> documentList = new ArrayList<>();
        documentList.addAll(new ArrayList<String>(Arrays.asList(document)));
        dataMap.put("document",documentList);

        String[] table = { "xls","xlsx","xlsm","xltx","xltm","xlsb","xlam" };
        List<String> tableList = new ArrayList<>();
        tableList.addAll(new ArrayList<String>(Arrays.asList(table)));
        dataMap.put("table",tableList);

        String[] video = { "mov","avi","rmvb","rm","flv","mp4","3GP" };
        List<String> videoList = new ArrayList<>();
        videoList.addAll(new ArrayList<String>(Arrays.asList(video)));
        dataMap.put("video",videoList);
        dataMap.put("other",new ArrayList<>());

        String data = JSON.toJSONString(dataMap);

        System.out.println("***********************data:"+ data);


    }

}
