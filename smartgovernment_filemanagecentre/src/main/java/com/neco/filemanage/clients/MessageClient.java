package com.neco.filemanage.clients;

import com.neco.messagecentre.dto.MessageDto;
import com.neco.sglog.annotation.SgLog;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("smartgovernmant-messagecentre")
public interface MessageClient {

    @PostMapping("/mesage/sendObj")
    String sendMessageByObj(@RequestBody MessageDto message);
}
