package org.mini_payment_service.paymentservice.dto;

import org.mini_payment_service.paymentservice.model.PaymentStatus;

public class PaymentResponse {

    private String paymentId;
    private PaymentStatus status;


    public PaymentResponse() {
    }

    public PaymentResponse(String paymentId, PaymentStatus status) {
        this.paymentId = paymentId;
        this.status = status;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
