package com.food.ordering.system.resatuarant.service.domain;

import com.food.ordering.system.resatuarant.service.domain.entity.Restaurant;
import com.food.ordering.system.resatuarant.service.domain.event.OrderApprovalEvent;

import java.util.List;

public interface RestaurantDomainService {
    OrderApprovalEvent validateOrder(
            Restaurant restaurant,
            List<String> failureMessages
    );
}
