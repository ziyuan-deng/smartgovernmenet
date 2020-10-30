package com.neco.filemanage.event;

import com.neco.filemanagecentre.model.BigFiles;
import org.springframework.context.ApplicationEvent;

/**
 * 合并各个文件块组成大文件
 * @author ziyuan_deng
 * @date 2020/10/10
 */
public class MergeBigFileEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public MergeBigFileEvent(BigFiles source) {
        super(source);
    }
}
