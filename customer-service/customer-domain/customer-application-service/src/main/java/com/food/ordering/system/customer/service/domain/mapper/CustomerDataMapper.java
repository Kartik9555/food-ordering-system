package com.food.ordering.system.customer.service.domain.mapper;

import com.food.ordering.system.customer.service.domain.create.CreateCustomerCommand;
import com.food.ordering.system.customer.service.domain.create.CreateCustomerResponse;
import com.food.ordering.system.customer.service.domain.entity.Customer;
import com.food.ordering.system.domain.valueobject.CustomerId;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataMapper {

    public Customer createCustomerCommandToCustomer(CreateCustomerCommand customerCommand) {
        return Customer.builder()
                .customerId(new CustomerId(customerCommand.getCustomerId()))
                .username(customerCommand.getUsername())
                .firstname(customerCommand.getFirstname())
                .lastname(customerCommand.getLastname())
                .build();
    }

    public CreateCustomerResponse customerToCreateCustomerResponse(Customer customer, String message) {
        return CreateCustomerResponse.builder()
                .customerId(customer.getId().getValue())
                .message(message)
                .build();
    }
}
