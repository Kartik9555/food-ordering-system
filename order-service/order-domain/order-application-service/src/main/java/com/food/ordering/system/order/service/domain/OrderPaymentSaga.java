package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.valueobject.OrderStatus;
import com.food.ordering.system.domain.valueobject.PaymentStatus;
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.system.order.service.domain.outbox.scheduler.approval.ApprovalOutboxHelper;
import com.food.ordering.system.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;
import com.food.ordering.system.saga.SagaSteps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.food.ordering.system.domain.DomainConstants.UTC;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaymentSaga implements SagaSteps<PaymentResponse> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final OrderDataMapper orderDataMapper;

    @Override
    @Transactional
    public void process(PaymentResponse paymentResponse) {
        Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse = paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                UUID.fromString(paymentResponse.getSagaId()), SagaStatus.STARTED
        );

        if (orderPaymentOutboxMessageResponse.isEmpty()) {
            log.info("An outbox message with saga id: {} is already processed!", paymentResponse.getSagaId());
            return;
        }
        OrderPaymentOutboxMessage outboxMessage = orderPaymentOutboxMessageResponse.get();
        OrderPaidEvent orderPaidEvent = completePaymentForOrder(paymentResponse);


        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(orderPaidEvent.getOrder().getOrderStatus());
        paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(outboxMessage, orderPaidEvent.getOrder().getOrderStatus(), sagaStatus));

        approvalOutboxHelper.saveApprovalOutboxMessage(
                orderDataMapper.orderPaidEventToOrderApprovalEventPayload(orderPaidEvent),
                orderPaidEvent.getOrder().getOrderStatus(),
                sagaStatus,
                OutboxStatus.STARTED,
                UUID.fromString(paymentResponse.getSagaId())
        );

        log.info("Order with order id: {} is paid", orderPaidEvent.getOrder().getId().getValue());
    }

    @Override
    @Transactional
    public void rollback(PaymentResponse paymentResponse) {
        Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse = paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                UUID.fromString(paymentResponse.getSagaId()), getCurrentSagaStatus(paymentResponse.getPaymentStatus())
        );
        if (orderPaymentOutboxMessageResponse.isEmpty()) {
            log.info("An outbox message with saga id: {} is already rolled back!", paymentResponse.getSagaId());
            return;
        }
        OrderPaymentOutboxMessage outboxMessage = orderPaymentOutboxMessageResponse.get();
        Order order = rollbackPaymentForOrder(paymentResponse);
        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getOrderStatus());
        paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(outboxMessage, order.getOrderStatus(), sagaStatus));

        if (paymentResponse.getPaymentStatus() == PaymentStatus.CANCELLED) {
            approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(paymentResponse.getSagaId(), order.getOrderStatus(), sagaStatus));
        }
        log.info("Order with order id: {} is cancelled", order.getId().getValue());
    }

    private SagaStatus[] getCurrentSagaStatus(PaymentStatus paymentStatus) {
        return switch (paymentStatus) {
            case COMPLETED -> new SagaStatus[]{SagaStatus.STARTED};
            case CANCELLED -> new SagaStatus[]{SagaStatus.PROCESSING};
            case FAILED -> new SagaStatus[]{SagaStatus.STARTED, SagaStatus.PROCESSING};

        };
    }

    private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(
            OrderPaymentOutboxMessage outboxMessage,
            OrderStatus orderStatus,
            SagaStatus sagaStatus
    ) {
        outboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
        outboxMessage.setOrderStatus(orderStatus);
        outboxMessage.setSagaStatus(sagaStatus);
        return outboxMessage;
    }

    private OrderPaidEvent completePaymentForOrder(PaymentResponse paymentResponse) {
        log.info("Completing payment for order with id: {}", paymentResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
        OrderPaidEvent orderPaidEvent = orderDomainService.payOrder(order);
        orderSagaHelper.saveOrder(order);
        return orderPaidEvent;
    }

    private Order rollbackPaymentForOrder(PaymentResponse paymentResponse) {
        log.info("Cancelling order with id: {}", paymentResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
        orderDomainService.cancelOrder(order, paymentResponse.getFailureMessages());
        orderSagaHelper.saveOrder(order);
        return order;
    }

    private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(
            String sagaId,
            OrderStatus orderStatus,
            SagaStatus sagaStatus
    ) {
        Optional<OrderApprovalOutboxMessage> approvalOutboxMessageResponse = approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(UUID.fromString(sagaId), SagaStatus.COMPENSATING);
        if (approvalOutboxMessageResponse.isEmpty()) {
            throw new OrderDomainException("Approval Outbox message could not be found in " + SagaStatus.COMPENSATING.name() + "status.");
        }
        OrderApprovalOutboxMessage approvalOutboxMessage = approvalOutboxMessageResponse.get();
        approvalOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
        approvalOutboxMessage.setOrderStatus(orderStatus);
        approvalOutboxMessage.setSagaStatus(sagaStatus);
        return approvalOutboxMessage;
    }
}
