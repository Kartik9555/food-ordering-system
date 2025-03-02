package com.food.ordering.system.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessageHelper {

    private final ObjectMapper objectMapper;

    public <T> T getOrderEventPayload(String payload, Class<T> outboxType) {
        try {
            return objectMapper.readValue(payload, outboxType);
        } catch (JsonProcessingException e) {
            log.error("Could not read {} object", outboxType.getName(), e);
            throw new OrderDomainException("Could not read " + outboxType.getName() + " object", e);
        }
    }

    public <T, U> BiFunction<SendResult<String, T>, Throwable, Void> getKafkaCallback(
            String topicName,
            T avroModel,
            U outboxMessage,
            BiConsumer<U, OutboxStatus> outboxCallback,
            String orderId,
            String requestAvroModelName
    ) {
        // Passing a BiFunction to handle both result and exception
        return (result, ex) -> {
            if (ex != null) {
                log.error("Error while sending {} with message: {} and outbox type: {} to topic {}",
                        requestAvroModelName,
                        avroModel.toString(),
                        outboxMessage.getClass().getName(),
                        topicName,
                        ex
                );
                outboxCallback.accept(outboxMessage, OutboxStatus.FAILED);
            } else {
                RecordMetadata recordMetadata = result.getRecordMetadata();
                log.info("Received successful response from Kafka for order id: {} Topic: {} Partition: {} Offset: {} Timestamp: {}",
                        orderId,
                        recordMetadata.topic(),
                        recordMetadata.partition(),
                        recordMetadata.offset(),
                        recordMetadata.timestamp()
                );
                outboxCallback.accept(outboxMessage, OutboxStatus.COMPLETED);
            }
            return null;
        };
    }
}
