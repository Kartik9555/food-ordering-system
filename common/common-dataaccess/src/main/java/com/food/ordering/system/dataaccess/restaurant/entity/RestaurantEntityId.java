package com.food.ordering.system.dataaccess.restaurant.entity;

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
    private UUID productId;
    private UUID restaurantId;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RestaurantEntityId that)) return false;
        return Objects.equals(productId, that.productId) && Objects.equals(restaurantId, that.restaurantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, restaurantId);
    }
}
