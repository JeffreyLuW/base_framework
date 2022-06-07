package com.meide.controller;



//import com.meide.common.config.mosquitto.IMqttSender;
import com.meide.common.config.mosquitto.MosquittoTemplate;
//import com.meide.common.config.mosquitto.MqttReceiverConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MosquittoTestController {


//
//    final IMqttSender iMqttSender;
//    final MqttReceiverConfig mqttReceiverConfig;
    final MosquittoTemplate mosquittoTemplate;

    // 发送自定义消息内容（使用默认主题）
    @RequestMapping("/test1/{data}")
    public void test1(@PathVariable("data") String data) {
        mosquittoTemplate.senderByDefaultTopic(data);
    }

    // 发送自定义消息内容，且指定主题
    @RequestMapping("/test2/{topic}/{data}")
    public void test2(@PathVariable("topic") String topic, @PathVariable("data") String data) throws InterruptedException {
        // 测试重连
        //for(int i=0;i<100000;i++){
        mosquittoTemplate.senderBySpecifyTopic(data, topic);
            //Thread.sleep(3000);
        //}

    }

    @GetMapping("/test3")
    public void test() {
        // 添加一个或多个监听Topic
        mosquittoTemplate.addTopic("topic1");// 默认qos为1
        mosquittoTemplate.addTopicWithQos("topic2", 1);
        mosquittoTemplate.addTopic("topic3", "topic4");
        mosquittoTemplate.addTopicWithQos(new String[]{"topic5", "topic6"},new int[]{1, 1});
        // 删除一个或多个监听Topic
        mosquittoTemplate.removeTopic("topic1");
//        mqttReceiverConfig.adapter.removeTopic("topic2", "topic3");
    }






}