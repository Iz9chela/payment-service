package org.mini_payment_service.paymentservice.service;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mini_payment_service.paymentservice.dto.ErrorResponse;
import org.mini_payment_service.paymentservice.exception.PaymentNotFoundException;
import org.mini_payment_service.paymentservice.model.Payment;
import org.mini_payment_service.paymentservice.model.PaymentStatus;
import org.mini_payment_service.paymentservice.repository.PaymentRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.ErrorResponseException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Payment testPayment;
    private UUID testPaymentId;

    @BeforeEach
    void setUp() {
        testPaymentId= UUID.randomUUID();
        testPayment= new Payment(BigDecimal.valueOf(1500), "EUR", "Online course desc");
        testPayment.setId(testPaymentId);
    }

    @Test
    void createPayment_WithValidData_ShouldSavePayment() {
        when(paymentRepository.save(testPayment)).thenReturn(testPayment);

        Payment result = paymentService.createPayment(testPayment);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getAmount()).isEqualTo(BigDecimal.valueOf(1500));
        assertThat(result.getCurrency()).isEqualTo("EUR");
        assertThat(result.getDescription()).isEqualTo("Online course desc");
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.CREATED);

        verify(paymentRepository, times(1)).save(testPayment);

    }

    @Test
    void createPayment_WithInvalidData_ShouldThrowException() {

        testPayment.setAmount(BigDecimal.valueOf(-1));

        assertThatThrownBy(() -> paymentService.createPayment(testPayment))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount must be positive");


        verify(paymentRepository, never()).save(testPayment);

    }

    @Test
    void getPayment_WithValidId_ShouldReturnPayment() {

        String paymentIdString = testPaymentId.toString();
        when(paymentRepository.findById(testPaymentId)).thenReturn(Optional.of(testPayment));

        Payment result = paymentService.getPaymentById(paymentIdString);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testPaymentId);
        assertThat(result.getAmount()).isEqualTo(BigDecimal.valueOf(1500));
        assertThat(result.getCurrency()).isEqualTo("EUR");

        verify(paymentRepository, times(1)).findById(testPaymentId);

    }

    @Test
    void getPayment_WithNonExistingId_ShouldThrowException() {

        UUID nonExistingId = UUID.randomUUID();
        String paymentIdString = nonExistingId.toString();
        when(paymentRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPaymentById(paymentIdString))
                .isInstanceOf(PaymentNotFoundException.class)
                .hasMessageContaining("Payment not found with id: " + paymentIdString);

        verify(paymentRepository, times(1)).findById(nonExistingId);

    }

    @Test
    void getAllPayments_ShouldReturnAllPayments() {

        when(paymentRepository.findAll()).thenReturn(List.of(testPayment));

        List<Payment> result = paymentService.getAllPayments();
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(testPayment);

        verify(paymentRepository, times(1)).findAll();

    }

    @Test
    void updateStatus_FromCreatedToProcessing_ShouldSucceed(){

        String paymentIdString = testPaymentId.toString();
        testPayment.setStatus(PaymentStatus.CREATED);

        when(paymentRepository.findById(testPaymentId)).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment savedPayment = invocation.getArgument(0);
            return savedPayment;
        });

        Payment result = paymentService.updateStatus(paymentIdString, PaymentStatus.PROCESSING);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.PROCESSING);

        verify(paymentRepository, times(1)).findById(testPaymentId);
        verify(paymentRepository, times(1)).save(testPayment);

    }

    @Test
    void updateStatus_FromProcessingToCompleted_ShouldSucceed(){

        String paymentIdString = testPaymentId.toString();
        testPayment.setStatus(PaymentStatus.PROCESSING);

        when(paymentRepository.findById(testPaymentId)).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment savedPayment = invocation.getArgument(0);
            return savedPayment;
        });

        Payment result = paymentService.updateStatus(paymentIdString, PaymentStatus.COMPLETED);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.COMPLETED);

        verify(paymentRepository, times(1)).findById(testPaymentId);
        verify(paymentRepository, times(1)).save(testPayment);

    }

    @Test
    void updateStatus_FromCreatedToCompleted_ShouldFail(){

        String paymentIdString = testPaymentId.toString();
        testPayment.setStatus(PaymentStatus.CREATED);

        when(paymentRepository.findById(testPaymentId)).thenReturn(Optional.of(testPayment));

        assertThatThrownBy(() -> paymentService.updateStatus(paymentIdString, PaymentStatus.COMPLETED))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid status transition from CREATED to " + PaymentStatus.COMPLETED);

        verify(paymentRepository, times(1)).findById(testPaymentId);
        verify(paymentRepository, never()).save(testPayment);

    }

    @Disabled // TODO Fix test + add negative scenario
    @Test
    void updatePayment_WithValidId_ShouldSucceed(){

        String paymentIdString = testPaymentId.toString();

        when(paymentRepository.findById(testPaymentId)).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Payment updatedPayment = new Payment(BigDecimal.valueOf(2000), "UAH", "Updated description");

        Payment result = paymentService.updatePayment(paymentIdString, updatedPayment);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testPaymentId);
        assertThat(result.getAmount()).isEqualTo(BigDecimal.valueOf(2000));
        assertThat(result.getCurrency()).isEqualTo("UAH");
        assertThat(result.getDescription()).isEqualTo("Updated description");
        assertThat(result.getUpdatedAt()).isNotNull();

        verify(paymentRepository, times(1)).findById(testPaymentId);
        verify(paymentRepository, times(1)).save(updatedPayment);

    }

}