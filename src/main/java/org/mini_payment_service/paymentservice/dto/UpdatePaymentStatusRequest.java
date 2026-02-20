package org.mini_payment_service.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import org.mini_payment_service.paymentservice.model.PaymentStatus;

public class UpdatePaymentStatusRequest {

    @NotNull(message = "Status is required!")
    private PaymentStatus status;

    public UpdatePaymentStatusRequest() {
    }

    public UpdatePaymentStatusRequest(PaymentStatus status) {
        this.status = status;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
