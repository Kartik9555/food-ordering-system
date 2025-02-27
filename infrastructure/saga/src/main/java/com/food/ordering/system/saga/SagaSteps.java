package com.food.ordering.system.saga;

public interface SagaSteps<T> {
    void process(T data);

    void rollback(T data);
}
