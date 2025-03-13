package com.food.ordering.system.customer.service.dataaccess.customer.mapper;

import com.food.ordering.system.customer.service.dataaccess.customer.entity.CustomerEntity;
import com.food.ordering.system.customer.service.domain.entity.Customer;
import com.food.ordering.system.domain.valueobject.CustomerId;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataAccessMapper {
    public Customer customerEntityToCustomer(CustomerEntity customerEntity) {
        return Customer.builder()
                .customerId(new CustomerId(customerEntity.getId()))
                .username(customerEntity.getUsername())
                .firstname(customerEntity.getFirstname())
                .lastname(customerEntity.getLastname())
                .build();
    }

    public CustomerEntity customerToCustomerEntity(Customer customer) {
        return CustomerEntity.builder()
                .id(customer.getId().getValue())
                .username(customer.getUsername())
                .firstname(customer.getFirstname())
                .lastname(customer.getLastname())
                .build();
    }
}
