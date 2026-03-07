package org.mini_payment_service.paymentservice.mapper;

import org.mini_payment_service.paymentservice.dto.PaymentDto;
import org.mini_payment_service.paymentservice.dto.PaymentRequest;
import org.mini_payment_service.paymentservice.dto.PaymentResponse;
import org.mini_payment_service.paymentservice.model.Payment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PaymentMapper {

    public Payment toEntity(PaymentRequest paymentRequest) {

        Objects.requireNonNull(paymentRequest, "Payment must not be null");

        return new Payment(
                paymentRequest.getAmount(),
                paymentRequest.getCurrency(),
                paymentRequest.getDescription()
        );
    }

    public PaymentDto toDto(Payment payment) {

        Objects.requireNonNull(payment, "Payment must not be null");

        return new PaymentDto(
                payment.getPaymentId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getDescription(),
                payment.getStatus(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );


    }

    public PaymentResponse toResponse(Payment payment) {
        if (payment == null) {
            return null;
        }

        PaymentResponse paymentResponse = new PaymentResponse(payment.getPaymentId(), payment.getStatus());

        return paymentResponse;
    }

}
