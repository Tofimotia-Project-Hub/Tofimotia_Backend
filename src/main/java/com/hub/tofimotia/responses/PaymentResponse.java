package com.hub.tofimotia.responses;

import com.hub.tofimotia.enums.PaymentMethod;
import com.hub.tofimotia.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    
    private Long paymentId;
    private Long bookingId;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private String transactionId;
    private String reference;
    private String gatewayResponse;
    private String failureReason;
    private LocalDateTime createdAt;
    private String message;
    
    // For payment initiation
    private String authorizationUrl;
    private String accessCode;
}