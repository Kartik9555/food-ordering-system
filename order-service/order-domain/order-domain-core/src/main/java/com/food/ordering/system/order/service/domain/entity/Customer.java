package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.domain.entity.AggregateRoot;
import com.food.ordering.system.domain.valueobject.CustomerId;

public class Customer extends AggregateRoot<CustomerId> {
    private String username;
    private String firstname;
    private String lastname;

    public Customer(CustomerId customerId, String username, String firstname, String lastname) {
        super.setId(customerId);
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public Customer(CustomerId customerId) {
        super.setId(customerId);
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }
}
