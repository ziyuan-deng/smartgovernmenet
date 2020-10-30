package com.neco.filemanage.scheduletask;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.neco.filemanage.service.FileService;
import com.neco.filemanagecentre.model.CentreFileInfo;
import com.neco.utils.DateUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 删除文件定时任务
 * @author ziyuan_deng
 * @date 2020/9/28
 */
@Component
@Slf4j
public class ScheduleTaskService {
    @Autowired
    private FileService fileService;

    @Autowired
    private FastFileStorageClient storageClient;

    /**
     * 定时任务删除一年前上传的文件
     */
    @Scheduled(cron = "10 0 0 * * ?")
    public void deleteLastYearFiles(){
        Date beforeYearDate = DateUtils.getDateBefore(new Date(), DateUtils.getlastYearDayCount(new Date()));
        String beforeYearDateStr = DateUtils.formatSimpleStr(beforeYearDate);
        beforeYearDateStr = beforeYearDateStr + " 23:59:59";
        Date lastDate = DateUtils.stringToFullDate(beforeYearDateStr);
        //在beforeYearDateStr以前的文件都删除掉
        Wrapper<CentreFileInfo> entityWrapper = new EntityWrapper<>();
        entityWrapper.le("update_time",lastDate);
        List<CentreFileInfo> fileList = fileService.selectList(entityWrapper);

        if (CollectionUtils.isNotEmpty(fileList)) {
            Set<String> idSet = new HashSet<>(fileList.size());
            fileList.forEach(files->{
                String groupName = files.getGroupName();
                String remoteFileName = files.getRemoteFileName();
                storageClient.deleteFile(groupName,remoteFileName);
                //boolean flag = fileService.deleteById(files.getId());
                idSet.add(files.getId());
                log.info("文件(id为："+files.getId()+")："+files.getFileName()+"删除成功！");
            });
            fileService.deleteBatchIds(idSet);

        }
    }

}
