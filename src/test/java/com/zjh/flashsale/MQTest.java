package com.zjh.flashsale;

import com.zjh.flashsale.mq.RocketMQService;
import com.zjh.flashsale.services.FlashsaleActivityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class MQTest {
    @Autowired
    RocketMQService rocketMQService;

    @Autowired
    FlashsaleActivityService flashsaleActivityService;

    @Test
    public void sendMQTest() throws Exception {
        rocketMQService.sendMessage("test-zjh", "Hello World!" + new Date().toString()); }
}
