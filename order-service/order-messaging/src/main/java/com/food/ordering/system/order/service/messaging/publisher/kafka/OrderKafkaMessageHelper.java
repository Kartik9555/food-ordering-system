package com.food.ordering.system.order.service.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Slf4j
@Component
public class OrderKafkaMessageHelper {

    public <T> BiFunction<SendResult<String, T>, Throwable, Void> getKafkaCallback(String topicName, T requestAvroModel, String orderId, String requestAvroModelName) {
        // Passing a BiFunction to handle both result and exception
        return (result, ex) -> {
            if (ex != null) {
                log.error("Error while sending {} message {} to topic {}", requestAvroModelName, requestAvroModel.toString(), topicName);
            } else {
                RecordMetadata recordMetadata = result.getRecordMetadata();
                log.info("Received successful response from Kafka for order id: {} Topic: {} Offset: {} Timestamp: {}",
                        orderId,
                        recordMetadata.topic(),
                        recordMetadata.offset(),
                        recordMetadata.timestamp());
            }
            return null;
        };
    }
}
