package org.mini_payment_service.paymentservice.model;

import lombok.Getter;

@Getter
public enum PaymentStatus {

    CREATED,
    PROCESSING,
    FAILED,
    COMPLETED

}
