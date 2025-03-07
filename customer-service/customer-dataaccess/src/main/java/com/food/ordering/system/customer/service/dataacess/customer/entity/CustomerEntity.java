package com.food.ordering.system.customer.service.dataacess.customer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customers", schema = "customer")
@Entity
public class CustomerEntity {
    @Id
    private UUID id;
    private String username;
    private String firstname;
    private String lastname;
}
