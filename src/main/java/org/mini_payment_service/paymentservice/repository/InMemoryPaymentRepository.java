package org.mini_payment_service.paymentservice.repository;

import org.mini_payment_service.paymentservice.model.Payment;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

@Repository
public class InMemoryPaymentRepository implements PaymentRepository {

    public InMemoryPaymentRepository() {
    }

    private final Map<UUID, Payment> payments = new HashMap<>();

    {
        Payment p1 = new Payment(BigDecimal.valueOf(2200),
                "UAH",
                "Book payment");
        Payment p2 = new Payment( BigDecimal.valueOf(3100),
                "USD",
                "Study payment");
        Payment p3 = new Payment(BigDecimal.valueOf(100),
                "EUR",
                "Book payment");
        payments.put(UUID.fromString(p1.getPaymentId()), p1);
        payments.put(UUID.fromString(p2.getPaymentId()), p2);
        payments.put(UUID.fromString(p3.getPaymentId()), p3);
    }

    @Override
    public Payment save(Payment payment) {
        if (payment == null) {
            throw  new IllegalArgumentException("Payment can not be null!");
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
            return false;
        }
        return payments.containsKey(paymentId);
    }

    @Override
    public List<Payment> findAll() {
        return payments.values().stream().toList();
    }
}
