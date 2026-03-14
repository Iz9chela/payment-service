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
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Payment createPayment(Payment payment) {

        if (payment.getAmount() == null || payment.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        logger.info("Creating payment with amount: {} {}", payment.getAmount(), payment.getCurrency());
        Payment savedPayment = paymentRepository.save(payment);
        logger.info("Payment created with ID: {}", savedPayment.getId());
        return savedPayment;

    }

    @Transactional(readOnly = true)
    public Payment getPaymentById(String paymentId) {

        logger.debug("Retrieving Payment with ID: {}", paymentId);

        UUID id = parseUuid(paymentId);
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {

        return paymentRepository.findAll();

    }

    @Transactional
    public Payment updateStatus(String paymentId, PaymentStatus newStatus) {

        logger.info("Updating payment {} to status: {}", paymentId, newStatus);

        UUID uuid = parseUuid(paymentId);

        Payment payment = paymentRepository.findById(uuid)
                .orElseThrow(() -> {
                    logger.warn("Payment not found with ID: {}", paymentId);
                    return new PaymentNotFoundException(uuid);
                });

        PaymentStatus currentStatus = payment.getStatus();

        validateStatusTransition(currentStatus, newStatus);

        payment.setStatus(newStatus);

        Payment updatedPayment = paymentRepository.save(payment);

        logger.info("Payment {} status updated from {} to {}",
                paymentId, currentStatus, newStatus);

        return updatedPayment;

    }

    @Transactional
    public Payment updatePayment(String paymentId, Payment updatedPayment) {

        logger.info("Updating Payment with ID: {}", paymentId);
        UUID id = parseUuid(paymentId);

        if (updatedPayment.getAmount() == null || updatedPayment.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));


        if (payment.getStatus() == PaymentStatus.COMPLETED
                || payment.getStatus() == PaymentStatus.FAILED
                || payment.getStatus() == PaymentStatus.CANCELLED
                || payment.getStatus() == PaymentStatus.DELETED) {
            throw new IllegalArgumentException("Can not update payment with status " + payment.getStatus());
        }

        payment.setAmount(updatedPayment.getAmount());
        payment.setDescription(updatedPayment.getDescription());
        payment.setCurrency(updatedPayment.getCurrency());


        return paymentRepository.save(payment);

    }

    @Transactional
    public Payment deletePaymentById(String paymentId) {

        UUID uuid = parseUuid(paymentId);

        Payment payment = paymentRepository.findById(uuid)
                .orElseThrow(() -> new PaymentNotFoundException(uuid));

        payment.setStatus(PaymentStatus.DELETED);

        logger.warn("Deleting Payment with ID: {}", paymentId);

        return paymentRepository.save(payment);


    }

    private UUID parseUuid(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidPaymentIdException(id, e);
        }
    }

    private void validateStatusTransition(PaymentStatus current, PaymentStatus target) {

        switch (current) {
            case CREATED -> {
                if (!(target == PaymentStatus.PROCESSING || target == PaymentStatus.CANCELLED || target == PaymentStatus.DELETED)) {
                    throw new IllegalStateException("Invalid status transition from CREATED to " + target);
                }
            }
            case PROCESSING -> {
                if (!(target == PaymentStatus.COMPLETED || target == PaymentStatus.FAILED || target == PaymentStatus.DELETED)) {
                    throw new IllegalStateException("Invalid status transition from PROCESSING to " + target);
                }
            }
            case COMPLETED, FAILED, CANCELLED, DELETED -> {
                throw new IllegalStateException("Cannot change final payment status: " + current);
            }
        }
    }


}
