package com.food.ordering.system.resatuarant.service.domain.event;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.resatuarant.service.domain.entity.OrderApproval;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderApprovedEvent extends OrderApprovalEvent {
    public OrderApprovedEvent(
            OrderApproval orderApproval,
            RestaurantId restaurantId,
            List<String> failureMessages,
            ZonedDateTime createdAt,
            DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher
    ) {
        super(orderApproval, restaurantId, failureMessages, createdAt);
    }
}
