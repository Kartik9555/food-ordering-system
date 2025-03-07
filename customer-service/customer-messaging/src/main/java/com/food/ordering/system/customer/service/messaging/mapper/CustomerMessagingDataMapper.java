package com.food.ordering.system.customer.service.messaging.mapper;

import com.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import com.food.ordering.system.kafka.order.avro.model.CustomerAvroModel;
import org.springframework.stereotype.Component;

@Component
public class CustomerMessagingDataMapper {
    public CustomerAvroModel customerCreatedEventToCustomerAvroModel(CustomerCreatedEvent customerCreatedEvent) {
        return new CustomerAvroModel(
                customerCreatedEvent.getCustomer().getId().getValue(),
                customerCreatedEvent.getCustomer().getUsername(),
                customerCreatedEvent.getCustomer().getFirstname(),
                customerCreatedEvent.getCustomer().getLastname()
        );
    }
}
