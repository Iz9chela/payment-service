package org.mini_payment_service.paymentservice.repository;

import org.mini_payment_service.paymentservice.model.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(UUID paymentId);

    boolean existsById(UUID paymentId);

    List<Payment> findAll();

    void deleteById(UUID paymentId);

}
