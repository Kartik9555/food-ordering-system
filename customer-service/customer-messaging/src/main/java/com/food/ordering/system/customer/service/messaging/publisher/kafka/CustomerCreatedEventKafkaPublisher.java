package com.food.ordering.system.customer.service.messaging.publisher.kafka;

import com.food.ordering.system.customer.service.domain.config.CustomerServiceConfigData;
import com.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import com.food.ordering.system.customer.service.domain.ports.output.message.publisher.CustomerMessagePublisher;
import com.food.ordering.system.customer.service.messaging.mapper.CustomerMessagingDataMapper;
import com.food.ordering.system.kafka.order.avro.model.CustomerAvroModel;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerCreatedEventKafkaPublisher implements CustomerMessagePublisher {

    private final CustomerMessagingDataMapper customerMessagingDataMapper;
    private final KafkaProducer<String, CustomerAvroModel> kafkaProducer;
    private final CustomerServiceConfigData customerServiceConfigData;

    @Override
    public void publish(CustomerCreatedEvent customerCreatedEvent) {
        log.info("Received CustomerCreatedEvent for customer id: {}", customerCreatedEvent.getCustomer().getId());

        try {
            CustomerAvroModel customerAvroModel = customerMessagingDataMapper.customerCreatedEventToCustomerAvroModel(customerCreatedEvent);
            kafkaProducer.send(
                    customerServiceConfigData.getCustomerTopicName(),
                    customerAvroModel.getId().toString(),
                    customerAvroModel,
                    getCallback(customerServiceConfigData.getCustomerTopicName(), customerAvroModel)
            );
            log.info("CustomerCreatedEvent sent to kafka for customer id: {}", customerAvroModel.getId());
        } catch (Exception e) {
            log.error("Error while sending CustomerCreatedEvent to kafka for customer id: {}, error: {}", customerCreatedEvent.getCustomer().getId(), e.getMessage());
        }
    }

    private BiFunction<SendResult<String, CustomerAvroModel>, Throwable, Void> getCallback(String customerTopicName, CustomerAvroModel customerAvroModel) {
        // Passing a BiFunction to handle both result and exception
        return (result, ex) -> {
            if (ex != null) {
                log.error("Error while sending message {} to topic {}", customerAvroModel.toString(), customerTopicName);
            } else {
                RecordMetadata recordMetadata = result.getRecordMetadata();
                log.info("Received new metadata. Topic: {}; Partition: {}; Offset: {} Timestamp: {}, at time {}",
                        recordMetadata.topic(),
                        recordMetadata.partition(),
                        recordMetadata.offset(),
                        recordMetadata.timestamp(),
                        System.nanoTime()
                );
            }
            return null;
        };
    }
}
