package com.alpha_code.alpha_code_robot_service.service;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.function.BiConsumer;

public interface MqttService {
    void publish(String topic, String payload) throws MqttException;
    void subscribe(String topic, BiConsumer<String, String> callback);
}