package org.mini_payment_service.paymentservice.exception;

public class InvalidPaymentIdException extends RuntimeException {

    public InvalidPaymentIdException(String paymentId) {
        super(String.format("Invalid payment ID format: %s. Expected valid UUID.", paymentId));
    }

    public InvalidPaymentIdException(String paymentId, Throwable cause) {
        super(String.format("Invalid payment ID format: %s. Expected valid UUID.", paymentId), cause);
    }
}
