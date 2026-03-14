package org.mini_payment_service.paymentservice.repository;

import org.mini_payment_service.paymentservice.model.Payment;
import org.mini_payment_service.paymentservice.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByCurrencyAndStatus(String currency, PaymentStatus status);
    Optional<Payment> findByIdAndStatus(UUID id, PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.amount > :minAmount")
    List<Payment> findByAmountGreaterThan(@Param("minAmount") BigDecimal minAmount);

}
