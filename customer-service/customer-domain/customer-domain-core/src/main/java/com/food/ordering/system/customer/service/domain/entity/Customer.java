package com.food.ordering.system.customer.service.domain.entity;

import com.food.ordering.system.domain.entity.AggregateRoot;
import com.food.ordering.system.domain.valueobject.CustomerId;

public class Customer extends AggregateRoot<CustomerId> {
    private final String username;
    private final String firstname;
    private final String lastname;

    private Customer(Builder builder) {
        setId(builder.customerId);
        username = builder.username;
        firstname = builder.firstname;
        lastname = builder.lastname;
    }

    public static Builder builder() {
        return new Builder();
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

    public static final class Builder {
        private CustomerId customerId;
        private String username;
        private String firstname;
        private String lastname;

        private Builder() {
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder username(String val) {
            username = val;
            return this;
        }

        public Builder firstname(String val) {
            firstname = val;
            return this;
        }

        public Builder lastname(String val) {
            lastname = val;
            return this;
        }

        public Customer build() {
            return new Customer(this);
        }
    }
}
