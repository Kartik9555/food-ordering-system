package com.food.ordering.system.kafka.producer.service;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.support.SendResult;

import java.io.Serializable;
import java.util.function.BiFunction;

public interface KafkaProducer<K extends Serializable, V extends SpecificRecordBase> {
    void send(String topicName, K key, V message, BiFunction<SendResult<K, V>, Throwable, Void> callback);
}