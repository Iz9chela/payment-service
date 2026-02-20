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

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentId(payment.getPaymentId());
        paymentDto.setCurrency(payment.getCurrency());
        paymentDto.setDescription(payment.getDescription());
        paymentDto.setStatus(payment.getStatus());
        paymentDto.setAmount(payment.getAmount());
        paymentDto.setCreatedAt(payment.getCreatedAt());
        paymentDto.setUpdatedAt(payment.getUpdatedAt());

        return paymentDto;


    }

    public PaymentResponse toResponse(Payment payment) {
        if (payment == null) {
            return null;
        }

        PaymentResponse paymentResponse = new PaymentResponse(payment.getPaymentId(), payment.getStatus());

        return paymentResponse;
    }

}
