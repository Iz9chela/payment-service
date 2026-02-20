package org.mini_payment_service.paymentservice.service;

import org.mini_payment_service.paymentservice.exception.InvalidPaymentIdException;
import org.mini_payment_service.paymentservice.exception.PaymentNotFoundException;
import org.mini_payment_service.paymentservice.model.Payment;
import org.mini_payment_service.paymentservice.model.PaymentStatus;
import org.mini_payment_service.paymentservice.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }


    public Payment createPayment(Payment payment) {

        if (payment.getAmount() == null || payment.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        logger.info("Creating payment with amount: {} {}", payment.getAmount(), payment.getCurrency());
        Payment savedPayment = paymentRepository.save(payment);
        logger.info("Payment created with ID: {}", savedPayment.getPaymentId());
        return savedPayment;

    }

    public Payment getPaymentById(String paymentId) {

        logger.debug("Retrieving Payment with ID: {}", paymentId);

        UUID id = parseUuid(paymentId);
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }


    public List<Payment> getAllPayments() {

        return paymentRepository.findAll();

    }

    public Payment updateStatus(String paymentId, PaymentStatus status){

        logger.info("Updating payment {} to status: {}", paymentId, status);

        UUID uuid = parseUuid(paymentId);

        Payment payment = paymentRepository.findById(uuid)
                .orElseThrow(() -> {
                    logger.warn("Payment not found with ID: {}", paymentId);
                    return new PaymentNotFoundException(uuid);
                });

        PaymentStatus oldStatus = payment.getStatus();

        if (oldStatus == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot change status of completed payment");
        }

        payment.setStatus(status);
        payment.setUpdatedAt(LocalDateTime.now());

        Payment updatedPayment = paymentRepository.save(payment);

        logger.info("Payment {} status updated from {} to {}",
                paymentId, oldStatus, status);

        return updatedPayment;

    }

    private UUID parseUuid(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidPaymentIdException(id, e);
        }
    }


}
