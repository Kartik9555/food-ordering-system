package com.food.ordering.system.resatuarant.service.domain;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.resatuarant.service.domain.entity.Restaurant;
import com.food.ordering.system.resatuarant.service.domain.event.OrderApprovalEvent;
import com.food.ordering.system.resatuarant.service.domain.event.OrderApprovedEvent;
import com.food.ordering.system.resatuarant.service.domain.event.OrderRejectedEvent;

import java.util.List;

public interface RestaurantDomainService {
    OrderApprovalEvent validateOrder(
            Restaurant restaurant,
            List<String> failureMessages,
            DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher,
            DomainEventPublisher<OrderRejectedEvent> orderRejectedEventDomainEventPublisher
    );
}
