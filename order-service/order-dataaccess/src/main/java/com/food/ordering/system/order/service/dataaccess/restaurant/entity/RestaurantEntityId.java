package com.food.ordering.system.order.service.dataaccess.restaurant.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantEntityId implements Serializable {
    private UUID id;
    private UUID restaurantId;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RestaurantEntityId that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(restaurantId, that.restaurantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, restaurantId);
    }
}
