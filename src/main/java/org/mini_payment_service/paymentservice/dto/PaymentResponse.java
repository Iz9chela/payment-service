package org.mini_payment_service.paymentservice.dto;

import org.mini_payment_service.paymentservice.model.PaymentStatus;

import java.util.UUID;

public class PaymentResponse {

    private UUID id;
    private PaymentStatus status;


    public PaymentResponse() {
    }

    public PaymentResponse(UUID id, PaymentStatus status) {
        this.id = id;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
