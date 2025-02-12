package com.food.ordering.system.kafka.producer.service.impl;

import com.food.ordering.system.kafka.producer.exception.KafkaProducerException;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerImpl<K extends Serializable, V extends SpecificRecordBase> implements KafkaProducer<K, V> {
    private final KafkaTemplate<K, V> kafkaTemplate;

    @Override
    public void send(String topicName, K key, V message, BiFunction<SendResult<K, V>, Throwable, Void> callback) {
        log.info("Sending message={} to topic={}", message, topicName);
        try {
            CompletableFuture<SendResult<K, V>> future = kafkaTemplate.send(topicName, key, message);
            future.handle(callback);
        } catch (Exception e) {
            log.error("Error on kafka producer with key: {}, message: {} and exception: {} ", key, message, e.getMessage());
            throw new KafkaProducerException("Error on kafka producer with key: " + key + ", message: " + message);
        }
    }

    @PreDestroy
    public void close() {
        if (kafkaTemplate != null) {
            log.info("Closing kafka producer!");
            kafkaTemplate.destroy();
        }
    }
}