package com.hub.tofimotia.services;

import com.hub.tofimotia.enums.PaymentMethod;
import com.hub.tofimotia.enums.PaymentStatus;
import com.hub.tofimotia.requests.PaymentRequest;
import com.hub.tofimotia.responses.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

/**
 * Mock Payment Service for development and testing
 * Simulates real payment gateway behavior without actual transactions
 */
@Slf4j
@Service
public class MockPaymentService {
    
    private final Random random = new Random();
    
    /**
     * Simulate payment initialization
     */
    public PaymentResponse initializePayment(PaymentRequest request) {
        log.info("Mock: Initializing payment for booking {} with amount {}", 
                request.getBookingId(), request.getAmount());
        
        String reference = generateReference();
        String transactionId = generateTransactionId();
        
        // Simulate different scenarios based on card number or amount
        PaymentStatus status = determinePaymentStatus(request);
        
        PaymentResponse.PaymentResponseBuilder responseBuilder = PaymentResponse.builder()
                .bookingId(request.getBookingId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .reference(reference)
                .transactionId(transactionId)
                .status(status);
        
        if (status == PaymentStatus.PENDING) {
            responseBuilder
                    .authorizationUrl("https://mock-gateway.com/pay/" + reference)
                    .accessCode("AC_" + reference)
                    .message("Payment initialized successfully");
        } else if (status == PaymentStatus.FAILED) {
            responseBuilder
                    .failureReason("Insufficient funds")
                    .message("Payment failed");
        }
        
        return responseBuilder.build();
    }
    
    /**
     * Simulate payment verification
     */
    public PaymentResponse verifyPayment(String reference) {
        log.info("Mock: Verifying payment with reference {}", reference);
        
        // Simulate verification delay
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate different verification outcomes
        PaymentStatus status = simulateVerificationOutcome();
        
        PaymentResponse.PaymentResponseBuilder responseBuilder = PaymentResponse.builder()
                .reference(reference)
                .transactionId("TXN_" + reference)
                .status(status);
        
        switch (status) {
            case COMPLETED:
                responseBuilder
                        .gatewayResponse("Payment successful")
                        .message("Payment completed successfully");
                break;
            case FAILED:
                responseBuilder
                        .failureReason("Transaction declined by bank")
                        .message("Payment verification failed");
                break;
            case PROCESSING:
                responseBuilder
                        .message("Payment is still being processed");
                break;
        }
        
        return responseBuilder.build();
    }
    
    /**
     * Simulate refund processing
     */
    public PaymentResponse processRefund(String transactionId, String reason) {
        log.info("Mock: Processing refund for transaction {} with reason: {}", transactionId, reason);
        
        // Simulate refund processing time
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String refundReference = "REF_" + generateReference();
        
        return PaymentResponse.builder()
                .transactionId(transactionId)
                .reference(refundReference)
                .status(PaymentStatus.REFUNDED)
                .gatewayResponse("Refund processed successfully")
                .message("Refund completed")
                .build();
    }
    
    private PaymentStatus determinePaymentStatus(PaymentRequest request) {
        // Simulate different scenarios for testing
        if (request.getPaymentMethod() == PaymentMethod.MOCK_PAYMENT) {
            return PaymentStatus.PENDING;
        }
        
        // Use card number patterns for predictable testing
        if (request.getCardNumber() != null) {
            if (request.getCardNumber().endsWith("0000")) {
                return PaymentStatus.FAILED;
            } else if (request.getCardNumber().endsWith("1111")) {
                return PaymentStatus.PROCESSING;
            }
        }
        
        // Random outcome for other cases (80% success rate)
        return random.nextInt(10) < 8 ? PaymentStatus.PENDING : PaymentStatus.FAILED;
    }
    
    private PaymentStatus simulateVerificationOutcome() {
        // Simulate verification outcomes (85% success rate)
        int outcome = random.nextInt(100);
        if (outcome < 85) {
            return PaymentStatus.COMPLETED;
        } else if (outcome < 95) {
            return PaymentStatus.FAILED;
        } else {
            return PaymentStatus.PROCESSING;
        }
    }
    
    private String generateReference() {
        return "MOCK_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private String generateTransactionId() {
        return "TXN_" + System.currentTimeMillis() + "_" + random.nextInt(1000);
    }
}