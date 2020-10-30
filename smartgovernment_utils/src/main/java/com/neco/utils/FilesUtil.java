package com.neco.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

/**
 * @author NIXQ
 */
public class FilesUtil {
    public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception {
        File targetFile = new File(filePath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(filePath + fileName);
        out.write(file);
        out.flush();
        out.close();
    }

    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 删除文件及文件夹
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        //判断路径是否存在
        if(file.exists()) {
            //boolean isFile():测试此抽象路径名表示的文件是否是一个标准文件。
            if(file.isFile()){
                file.delete();
            }else{
                //不是文件，对于文件夹的操作
                //listFiles方法：返回file路径下所有文件和文件夹的绝对路径
                File[] listFiles = file.listFiles();
                for (File file2 : listFiles) {
                    /*
                     * 递归作用：由外到内先一层一层删除里面的文件 再从最内层 反过来删除文件夹
                     *    注意：此时的文件夹在上一步的操作之后，里面的文件内容已全部删除
                     *         所以每一层的文件夹都是空的  ==》最后就可以直接删除了
                     */
                    deleteFile(file2);
                }
            }
            file.delete();
            return true;
        }else {
            return false;
        }

    }


    public static String renameToUUID(String fileName) {
        return UUID.randomUUID() + "." + fileName.substring(fileName.lastIndexOf(".") + 1);
    }


}



