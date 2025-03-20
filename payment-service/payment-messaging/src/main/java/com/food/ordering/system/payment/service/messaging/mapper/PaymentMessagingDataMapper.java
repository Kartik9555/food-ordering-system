package com.food.ordering.system.payment.service.messaging.mapper;

import com.food.ordering.system.domain.event.payload.OrderPaymentEventPayload;
import com.food.ordering.system.domain.valueobject.PaymentOrderStatus;
import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import debezium.order.payment_outbox.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PaymentMessagingDataMapper {

    public PaymentRequest paymentRequestAvroModelToPaymentRequest(
            OrderPaymentEventPayload orderPaymentEventPayload,
            Value paymentRequestAvroModel
    ) {
        return PaymentRequest.builder()
                .id(paymentRequestAvroModel.getId())
                .sagaId(paymentRequestAvroModel.getSagaId())
                .customerId(orderPaymentEventPayload.getCustomerId())
                .orderId(orderPaymentEventPayload.getOrderId())
                .paymentOrderStatus(PaymentOrderStatus.valueOf(orderPaymentEventPayload.getPaymentOrderStatus()))
                .price(orderPaymentEventPayload.getPrice())
                .createdAt(Instant.parse(paymentRequestAvroModel.getCreatedAt()))
                .build();
    }
}
