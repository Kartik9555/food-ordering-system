package com.food.ordering.system.customer.service.application.rest;

import com.food.ordering.system.customer.service.domain.create.CreateCustomerCommand;
import com.food.ordering.system.customer.service.domain.create.CreateCustomerResponse;
import com.food.ordering.system.customer.service.domain.ports.input.service.CustomerApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/customers", produces = "application/vnd.api.v1+json")
public class CustomerController {

    private final CustomerApplicationService customerApplicationService;

    @PostMapping
    public ResponseEntity<CreateCustomerResponse> createCustomer(@RequestBody CreateCustomerCommand customerCommand) {
        log.info("Creating customer with username: {}", customerCommand.getUsername());
        CreateCustomerResponse customer = customerApplicationService.createCustomer(customerCommand);
        return ResponseEntity.ok(customer);
    }
}
