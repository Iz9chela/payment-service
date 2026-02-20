package org.mini_payment_service.paymentservice.exception;

import java.util.UUID;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(UUID paymentId) {
        super(String.format("Payment not found with id: %s", paymentId));
    }

    public PaymentNotFoundException(String message) {
        super(message);
    }
}
