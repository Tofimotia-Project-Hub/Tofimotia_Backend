package com.hub.tofimotia.controllers;

import com.hub.tofimotia.requests.PaymentRequest;
import com.hub.tofimotia.responses.ApiResponse;
import com.hub.tofimotia.responses.PaymentResponse;
import com.hub.tofimotia.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management", description = "APIs for payment processing and management")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping("/initiate")
    @Operation(summary = "Initiate Payment", description = "Initialize payment for a booking")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiatePayment(
            @Valid @RequestBody PaymentRequest request) {
        
        PaymentResponse response = paymentService.initiatePayment(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment initiated successfully"));
    }
    
    @GetMapping("/verify/{reference}")
    @Operation(summary = "Verify Payment", description = "Verify payment status using reference")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> verifyPayment(
            @PathVariable String reference) {
        
        PaymentResponse response = paymentService.verifyPayment(reference);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment verification completed"));
    }
    
    @PostMapping("/{paymentId}/refund")
    @Operation(summary = "Process Refund", description = "Process refund for a completed payment")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> processRefund(
            @PathVariable Long paymentId,
            @RequestParam String reason) {
        
        PaymentResponse response = paymentService.processRefund(paymentId, reason);
        return ResponseEntity.ok(ApiResponse.success(response, "Refund processed successfully"));
    }
    
    @GetMapping("/my-payments")
    @Operation(summary = "Get User Payments", description = "Get payment history for current user")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getUserPayments(
            Authentication authentication) {
        
        // Extract user ID from authentication (you'll need to implement this based on your JWT setup)
        Long userId = extractUserIdFromAuth(authentication);
        List<PaymentResponse> payments = paymentService.getUserPayments(userId);
        return ResponseEntity.ok(ApiResponse.success(payments, "User payments retrieved successfully"));
    }
    
    @GetMapping("/vendor-payments")
    @Operation(summary = "Get Vendor Payments", description = "Get payment history for current vendor")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getVendorPayments(
            Authentication authentication) {
        
        Long vendorId = extractUserIdFromAuth(authentication);
        List<PaymentResponse> payments = paymentService.getVendorPayments(vendorId);
        return ResponseEntity.ok(ApiResponse.success(payments, "Vendor payments retrieved successfully"));
    }
    
    @GetMapping("/reference/{reference}")
    @Operation(summary = "Get Payment by Reference", description = "Get payment details by reference")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByReference(
            @PathVariable String reference) {
        
        PaymentResponse response = paymentService.getPaymentByReference(reference);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment details retrieved successfully"));
    }
    
    // Mock webhook endpoint for testing
    @PostMapping("/webhook/mock")
    @Operation(summary = "Mock Payment Webhook", description = "Mock webhook endpoint for testing payment notifications")
    public ResponseEntity<ApiResponse<String>> handleMockWebhook(
            @RequestBody String payload) {
        
        // In a real implementation, you would verify the webhook signature
        // and process the payment status update
        return ResponseEntity.ok(ApiResponse.success("Webhook processed", "Mock webhook received"));
    }
    
    private Long extractUserIdFromAuth(Authentication authentication) {
        // This is a placeholder - implement based on your JWT token structure
        // You might need to cast to your custom UserDetails implementation
        return 1L; // Replace with actual user ID extraction
    }
}