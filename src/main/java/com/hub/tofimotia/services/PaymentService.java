package com.hub.tofimotia.services;

import com.hub.tofimotia.models.BookingStatus;
import com.hub.tofimotia.enums.PaymentStatus;
import com.hub.tofimotia.models.Bookings;
import com.hub.tofimotia.models.Payment;
import com.hub.tofimotia.repositories.BookingRepository;
import com.hub.tofimotia.repositories.PaymentRepository;
import com.hub.tofimotia.requests.PaymentRequest;
import com.hub.tofimotia.responses.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final MockPaymentService mockPaymentService;
    
    @Transactional
    public PaymentResponse initiatePayment(PaymentRequest request) {
        log.info("Initiating payment for booking {}", request.getBookingId());
        
        // Validate booking exists and is in correct state
        Bookings booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Booking is not in a payable state");
        }
        
        // Check if payment already exists for this booking
        List<Payment> existingPayments = paymentRepository.findByBookingId(request.getBookingId());
        boolean hasSuccessfulPayment = existingPayments.stream()
                .anyMatch(p -> p.getStatus() == PaymentStatus.COMPLETED);
        
        if (hasSuccessfulPayment) {
            throw new RuntimeException("Payment already completed for this booking");
        }
        
        // Initialize payment with mock service
        PaymentResponse mockResponse = mockPaymentService.initializePayment(request);
        
        // Save payment record
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(mockResponse.getStatus());
        payment.setReference(mockResponse.getReference());
        payment.setTransactionId(mockResponse.getTransactionId());
        payment.setGatewayResponse(mockResponse.getMessage());
        
        payment = paymentRepository.save(payment);
        
        // Build response
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .bookingId(booking.getId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .reference(payment.getReference())
                .transactionId(payment.getTransactionId())
                .authorizationUrl(mockResponse.getAuthorizationUrl())
                .accessCode(mockResponse.getAccessCode())
                .message(mockResponse.getMessage())
                .createdAt(payment.getCreatedAt())
                .build();
    }
    
    @Transactional
    public PaymentResponse verifyPayment(String reference) {
        log.info("Verifying payment with reference {}", reference);
        
        Payment payment = paymentRepository.findByReference(reference)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            return buildPaymentResponse(payment, "Payment already verified");
        }
        
        // Verify with mock service
        PaymentResponse mockResponse = mockPaymentService.verifyPayment(reference);
        
        // Update payment status
        payment.setStatus(mockResponse.getStatus());
        payment.setGatewayResponse(mockResponse.getGatewayResponse());
        payment.setFailureReason(mockResponse.getFailureReason());
        
        if (mockResponse.getStatus() == PaymentStatus.COMPLETED) {
            // Update booking status to confirmed
            Bookings booking = payment.getBooking();
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
            log.info("Booking {} confirmed after successful payment", booking.getId());
        }
        
        payment = paymentRepository.save(payment);
        
        return buildPaymentResponse(payment, mockResponse.getMessage());
    }
    
    @Transactional
    public PaymentResponse processRefund(Long paymentId, String reason) {
        log.info("Processing refund for payment {} with reason: {}", paymentId, reason);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new RuntimeException("Can only refund completed payments");
        }
        
        // Process refund with mock service
        PaymentResponse mockResponse = mockPaymentService.processRefund(
                payment.getTransactionId(), reason);
        
        // Update payment status
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setGatewayResponse("Refunded: " + reason);
        payment = paymentRepository.save(payment);
        
        // Update booking status
        Bookings booking = payment.getBooking();
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        
        return buildPaymentResponse(payment, mockResponse.getMessage());
    }
    
    public List<PaymentResponse> getUserPayments(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(payment -> buildPaymentResponse(payment, null))
                .collect(Collectors.toList());
    }
    
    public List<PaymentResponse> getVendorPayments(Long vendorId) {
        return paymentRepository.findByVendorId(vendorId).stream()
                .map(payment -> buildPaymentResponse(payment, null))
                .collect(Collectors.toList());
    }
    
    public PaymentResponse getPaymentByReference(String reference) {
        Payment payment = paymentRepository.findByReference(reference)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return buildPaymentResponse(payment, null);
    }
    
    private PaymentResponse buildPaymentResponse(Payment payment, String message) {
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .bookingId(payment.getBooking().getId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .reference(payment.getReference())
                .transactionId(payment.getTransactionId())
                .gatewayResponse(payment.getGatewayResponse())
                .failureReason(payment.getFailureReason())
                .createdAt(payment.getCreatedAt())
                .message(message)
                .build();
    }
}