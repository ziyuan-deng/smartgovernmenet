package com.neco.filemanage.utils;

import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author ziyuan_deng
 * @date 2020/9/24
 */
public class FilesUtils {

    /**
     * 获取文件的Mime类型
     * @param fileUrl
     * @return
     * @throws java.io.IOException
     */
    public static String getMimeType(String fileUrl) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String type = fileNameMap.getContentTypeFor(fileUrl);
        if (type==null) {
            Path path = Paths.get(fileUrl);
            try {
                type = Files.probeContentType(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return type;
    }

/*    public static void main(String[] args) {
      //  String url = "G:\\pic\\js.png";
        String url = "G:\\事务所有关资料\\文件中心建设.docx";

        String mimeType = getMimeType(url);
        System.out.println("mimeType:"+mimeType);
        System.out.println("***********************");

        String contentType = getContentType(url);
        System.out.println("contentType:"+contentType);
    }*/
}
