package org.mini_payment_service.paymentservice.repository;

import org.mini_payment_service.paymentservice.exception.PaymentNotFoundException;
import org.mini_payment_service.paymentservice.model.Payment;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPaymentRepository implements PaymentRepository {

    public InMemoryPaymentRepository() {
    }

    private final Map<UUID, Payment> payments = new ConcurrentHashMap<>();

    {

        UUID randomUUID = UUID.randomUUID();

        Payment p1 = new Payment(BigDecimal.valueOf(2200),
                "UAH",
                "Book payment");
        p1.setPaymentId(randomUUID.toString());
        payments.put(randomUUID, p1);

        randomUUID = UUID.randomUUID();
        Payment p2 = new Payment(BigDecimal.valueOf(3100),
                "USD",
                "Study payment");
        p2.setPaymentId(randomUUID.toString());
        payments.put(randomUUID, p2);

        randomUUID = UUID.randomUUID();
        Payment p3 = new Payment(BigDecimal.valueOf(100),
                "EUR",
                "Book payment");
        p3.setPaymentId(randomUUID.toString());
        payments.put(randomUUID, p3);
    }

    @Override
    public Payment save(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment can not be null!");
        }
        payments.put(UUID.fromString(payment.getPaymentId()), payment);
        return payment;
    }

    @Override
    public Optional<Payment> findById(UUID paymentId) {
        if (paymentId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(payments.get(paymentId));
    }

    @Override
    public boolean existsById(UUID paymentId) {
        if (paymentId == null) {
            throw new IllegalArgumentException("Payment can not be null!");
        }
        return payments.containsKey(paymentId);
    }

    @Override
    public List<Payment> findAll() {
        return payments.values().stream().toList();
    }

    @Override
    public void deleteById(UUID paymentId) {
        if (existsById(paymentId)) {
            payments.remove(paymentId);
        } else {
            throw new PaymentNotFoundException(String.format("Payment with id: %s not found!", paymentId));
        }
    }
}
