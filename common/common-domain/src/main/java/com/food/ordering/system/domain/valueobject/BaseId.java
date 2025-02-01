package com.food.ordering.system.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public abstract class BaseId<T> {
    private final T value;
}
