package com.food.ordering.system.restaurant.service.dataaccess.outbox.adapter;

import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.restaurant.service.dataaccess.outbox.exception.OrderOutboxNotFoundException;
import com.food.ordering.system.restaurant.service.dataaccess.outbox.mapper.OrderOutboxDataAccessMapper;
import com.food.ordering.system.restaurant.service.dataaccess.outbox.repository.OrderOutboxJpaRepository;
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.OrderOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderOutboxRepositoryImpl implements OrderOutboxRepository {

    private final OrderOutboxJpaRepository orderOutboxJpaRepository;
    private final OrderOutboxDataAccessMapper orderOutboxDataAccessMapper;

    @Override
    public OrderOutboxMessage save(OrderOutboxMessage orderOutboxMessage) {
        return orderOutboxDataAccessMapper.orderOutboxEntityToOrderOutboxMessage(
                orderOutboxJpaRepository.save(orderOutboxDataAccessMapper.orderOutboxMessageToOrderOutboxEntity(orderOutboxMessage))
        );
    }

    @Override
    public Optional<List<OrderOutboxMessage>> findByTypeAndOutboxStatus(String sagaType, OutboxStatus outboxStatus) {
        return Optional.of(
                orderOutboxJpaRepository.findByTypeAndOutboxStatus(sagaType, outboxStatus)
                        .orElseThrow(() -> new OrderOutboxNotFoundException("Approval outbox object cannot be found for saga type " + sagaType))
                        .stream()
                        .map(orderOutboxDataAccessMapper::orderOutboxEntityToOrderOutboxMessage)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Optional<OrderOutboxMessage> findByTypeAndSagaIdAndOutboxStatus(String sagaType, UUID sagaId, OutboxStatus outboxStatus) {
        return orderOutboxJpaRepository.findByTypeAndSagaIdAndOutboxStatus(sagaType, sagaId, outboxStatus)
                .map(orderOutboxDataAccessMapper::orderOutboxEntityToOrderOutboxMessage);
    }

    @Override
    public void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus) {
        orderOutboxJpaRepository.deleteByTypeAndOutboxStatus(type, outboxStatus);
    }
}
