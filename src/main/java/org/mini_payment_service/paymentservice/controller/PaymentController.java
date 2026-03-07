package org.mini_payment_service.paymentservice.controller;

import jakarta.validation.Valid;
import org.mini_payment_service.paymentservice.dto.PaymentDto;
import org.mini_payment_service.paymentservice.dto.UpdatePaymentStatusRequest;
import org.mini_payment_service.paymentservice.mapper.PaymentMapper;
import org.mini_payment_service.paymentservice.model.Payment;
import org.mini_payment_service.paymentservice.dto.PaymentRequest;
import org.mini_payment_service.paymentservice.dto.PaymentResponse;
import org.mini_payment_service.paymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    public PaymentController(PaymentService paymentService, PaymentMapper paymentMapper) {
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {

        logger.info("Received payment creation request for amount: {} {}",
                request.getAmount(), request.getCurrency());

        Payment payment = paymentMapper.toEntity(request);
        Payment createdPayment = paymentService.createPayment(payment);
        PaymentResponse response = paymentMapper.toResponse(createdPayment);

        logger.info("Payment created successfully with ID: {}", response.getPaymentId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPayment(@PathVariable("id") String paymentId) {

        logger.info("Received request to fetch payment with ID: {}", paymentId);

        Payment payment = paymentService.getPaymentById(paymentId);
        PaymentDto dto = paymentMapper.toDto(payment);

        logger.info("Payment retrieved successfully: {}", paymentId);

        return ResponseEntity.ok(dto);

    }

    @GetMapping
    public ResponseEntity<List<PaymentDto>> getPayments() {

        List<PaymentDto> paymentDtoList = paymentService.getAllPayments()
                .stream()
                .map(paymentMapper::toDto)
                .toList();

        return ResponseEntity.ok(paymentDtoList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDto> updatePayment(@PathVariable("id") String paymentId, @Valid @RequestBody PaymentRequest request) {
        logger.info("Received request to update payment with ID: {}", paymentId);

        Payment payment = paymentMapper.toEntity(request);
        Payment updatedPayment = paymentService.updatePayment(paymentId, payment);
        PaymentDto dto = paymentMapper.toDto(updatedPayment);
        logger.info("Payment updated successfully with ID: {}", paymentId);
        return ResponseEntity.ok(dto);

    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentDto> updateStatus(
            @PathVariable("id") String paymentId,
            @Valid @RequestBody UpdatePaymentStatusRequest request) {

        logger.info("Received request to update payment {} status to: {}",
                paymentId, request.getStatus());

        Payment updatedPayment = paymentService.updateStatus(paymentId, request.getStatus());
        PaymentDto dto = paymentMapper.toDto(updatedPayment);

        logger.info("Payment {} status updated successfully to: {}",
                paymentId, request.getStatus());

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PaymentResponse> deletePayment(@PathVariable("id") String paymentId) {

        logger.info("Received delete request to delete payment with ID: {}", paymentId);

        Payment deleted = paymentService.deletePaymentById(paymentId);

        return ResponseEntity.ok(
                new PaymentResponse(deleted.getPaymentId(), deleted.getStatus())
        );


    }


}
