package com.meide.common.config.mosquitto;

import com.meide.common.config.mosquitto.IMqttSender;
import com.meide.common.config.mosquitto.MqttReceiverConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class MosquittoTemplate {

    final IMqttSender iMqttSender;
    final MqttReceiverConfig mqttReceiverConfig;

    /**
     * 发送消息
     * */

    // 发送自定义消息内容（使用默认主题）
    public void senderByDefaultTopic(String data){
        iMqttSender.sendToMqtt(data);
    }

    // 发送自定义消息内容，且指定主题
    public void senderBySpecifyTopic(String data,String topic){
        iMqttSender.sendToMqtt(topic, data);
    }

    // 发送自定义消息内容，且指定多个主题
    public void senderBySpecifyTopic(String data,String... topic){
        Arrays.stream(topic).forEach(t -> iMqttSender.sendToMqtt(t, data));
    }

    /**
     * topic订阅管理
     * */

    // 添加监听topic,默认qos为1
    public void addTopic(String... topic){
        mqttReceiverConfig.adapter.addTopic(topic);
    }

    // 添加监听topic,指定qos
    public void addTopicWithQos(String topic, int qos){
        mqttReceiverConfig.adapter.addTopic(topic, qos);
    }

    // 批量添加topic，并指定qos >> addTopic(topic[i], qos[i])
    public void addTopicWithQos(String[] topic, int[] qos){
        mqttReceiverConfig.adapter.addTopics(topic, qos);
    }

    // 删除topic
    public void removeTopic(String... topic){
        mqttReceiverConfig.adapter.removeTopic(topic);
    }

}
