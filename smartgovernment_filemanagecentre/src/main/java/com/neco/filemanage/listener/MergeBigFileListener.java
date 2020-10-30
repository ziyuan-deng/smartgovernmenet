package com.neco.filemanage.listener;

import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.neco.filemanage.enums.BigFileEnum;
import com.neco.filemanage.event.MergeBigFileEvent;
import com.neco.filemanagecentre.model.BigFiles;
import com.neco.filemanagecentre.model.CentreFileInfo;
import com.neco.utils.FilesUtil;
import com.neco.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * @author ziyuan_deng
 * @date 2020/10/10
 */
@Component
@Slf4j
public class MergeBigFileListener {

    private static final Long redisLongTime = 60*60*24L;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private AppendFileStorageClient fileStorageClient;

    /**
     * 通过事件机制处理大文件组合
     * @param event
     */
    @Async
    @Order
    @EventListener
    public void mergeFile(MergeBigFileEvent event) {
        BigFiles bigFiles = (BigFiles) event.getSource();
        File partherFile = bigFiles.getTempPath();
        try {
            List<Object> list = redisUtil.lGet(BigFileEnum.BIG_FILE_TOTAL.getValue()+bigFiles.getIdentifier(),0,-1);
            Integer start =  (Integer) redisUtil.get(BigFileEnum.BIG_FILE_NUMBER.getValue()+ bigFiles.getIdentifier());
            if (start == null){
                //说明未初始化
                return ;
            }
            list.sort((o1, o2) -> Integer.valueOf(o1.toString()) - Integer.valueOf(o2.toString()));
            CentreFileInfo files = (CentreFileInfo) redisUtil.get(BigFileEnum.BIG_FILE.getValue()+bigFiles.getIdentifier());
            //list的value从2开始，区块从1开始并放入dfs
            int size = list.size();
            for (int i = start.intValue(); i < size; i++,start++) {
                Integer index = Integer.valueOf(list.get(i).toString());
                if(i<(size-1) && Integer.valueOf(list.get(i).toString()) .equals( Integer.valueOf(list.get(i+1).toString()))){
                    continue;
                }
                if(index != i+2 ){
                    break;
                }
                File tempPartFile = new File( partherFile,bigFiles.getFilename() + "_" + index + ".part");
                InputStream is = new FileInputStream(tempPartFile);
                is.close();
                //按顺序追加文件
                fileStorageClient.appendFile(files.getGroupName(),files.getRemoteFileName(),is,files.getFileSize());
                /*FastDFSClientUtil.bigAppendUpload(
                        files.getGroupName(), FileUtils.readFileToByteArray(tempPartFile),files.getRemoteFileName());*/
            }
            redisUtil.set(BigFileEnum.BIG_FILE_NUMBER.getValue()+ bigFiles.getIdentifier(),start,redisLongTime);
            if(start .equals( bigFiles.getTotalChunks()-1)){
                delteRedisKey(bigFiles);
            }
        }catch(Exception e) {
            log.error("文件块合并失败：",e);
            e.printStackTrace();
        }
    }
    /**
     * 删除redis缓存
     */
    private void delteRedisKey(BigFiles bigFiles){
        redisUtil.expire(BigFileEnum.BIG_FILE_TOTAL.getValue()+bigFiles.getIdentifier(),60*60);
        redisUtil.expire(BigFileEnum.BIG_FILE.getValue()+ bigFiles.getIdentifier(),60*60);
        redisUtil.expire(BigFileEnum.BIG_FILE_NUMBER.getValue()+ bigFiles.getIdentifier(),60*60);
        FilesUtil.deleteFile(bigFiles.getTempPath());
    }
}
