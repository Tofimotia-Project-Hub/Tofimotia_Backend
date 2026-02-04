# Mock Payment System Guide

## Overview
This mock payment system simulates real payment gateway behavior without processing actual transactions. It's perfect for development, testing, and demo purposes.

## Features
- ✅ Payment initialization
- ✅ Payment verification
- ✅ Refund processing
- ✅ Multiple payment methods
- ✅ Predictable test scenarios
- ✅ Realistic response times
- ✅ Error simulation

## API Endpoints

### 1. Initiate Payment
```bash
POST /api/v1/payments/initiate
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "bookingId": 1,
  "amount": 50000.00,
  "paymentMethod": "MOCK_PAYMENT",
  "cardNumber": "4084084084084081",
  "expiryMonth": "12",
  "expiryYear": "2025",
  "cvv": "123",
  "cardHolderName": "John Doe",
  "customerEmail": "john@example.com",
  "customerPhone": "+2348012345678"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Payment initiated successfully",
  "data": {
    "paymentId": 1,
    "bookingId": 1,
    "amount": 50000.00,
    "status": "PENDING",
    "paymentMethod": "MOCK_PAYMENT",
    "reference": "MOCK_A1B2C3D4",
    "transactionId": "TXN_1234567890_123",
    "authorizationUrl": "https://mock-gateway.com/pay/MOCK_A1B2C3D4",
    "accessCode": "AC_MOCK_A1B2C3D4",
    "message": "Payment initialized successfully"
  }
}
```

### 2. Verify Payment
```bash
GET /api/v1/payments/verify/MOCK_A1B2C3D4
Authorization: Bearer YOUR_JWT_TOKEN
```

**Response:**
```json
{
  "success": true,
  "message": "Payment verification completed",
  "data": {
    "paymentId": 1,
    "bookingId": 1,
    "amount": 50000.00,
    "status": "COMPLETED",
    "reference": "MOCK_A1B2C3D4",
    "transactionId": "TXN_1234567890_123",
    "gatewayResponse": "Payment successful",
    "message": "Payment completed successfully"
  }
}
```

### 3. Process Refund
```bash
POST /api/v1/payments/1/refund?reason=Customer%20requested%20cancellation
Authorization: Bearer YOUR_JWT_TOKEN
```

### 4. Get User Payments
```bash
GET /api/v1/payments/my-payments
Authorization: Bearer YOUR_JWT_TOKEN
```

### 5. Get Vendor Payments
```bash
GET /api/v1/payments/vendor-payments
Authorization: Bearer YOUR_JWT_TOKEN
```

## Test Scenarios

### Card Number Patterns for Predictable Testing

| Card Number Ending | Expected Outcome |
|-------------------|------------------|
| `0000` | Payment fails immediately |
| `1111` | Payment goes to processing state |
| Any other | Random outcome (80% success rate) |

### Example Test Cards
- **Success**: `4084084084084081`
- **Failure**: `4084084084084000`
- **Processing**: `4084084084084111`

## Payment Statuses

| Status | Description |
|--------|-------------|
| `PENDING` | Payment initialized, awaiting verification |
| `PROCESSING` | Payment being processed by gateway |
| `COMPLETED` | Payment successful |
| `FAILED` | Payment failed |
| `CANCELLED` | Payment cancelled |
| `REFUNDED` | Payment refunded |

## Integration with Booking System

When a payment is completed:
1. Payment status changes to `COMPLETED`
2. Associated booking status changes to `CONFIRMED`
3. Booking becomes active and venue is reserved

When a refund is processed:
1. Payment status changes to `REFUNDED`
2. Associated booking status changes to `CANCELLED`
3. Venue becomes available again

## Mock Configuration

The system uses these configuration properties:

```properties
# Enable/disable mock mode
payment.mock-mode=true

# Success rate (0-100)
payment.mock.success-rate=85

# Processing delay in milliseconds
payment.mock.processing-delay=1000
```

## Switching to Real Payment Gateways

When ready to integrate real payment providers:

1. Set `payment.mock-mode=false` in configuration
2. Add real API keys to `application-payment.properties`
3. Implement actual gateway services (PaystackService, FlutterwaveService, etc.)
4. Update PaymentService to use real gateways instead of MockPaymentService

## Testing Workflow

1. **Create a booking** using the booking API
2. **Initiate payment** with the booking ID
3. **Verify payment** using the returned reference
4. **Check booking status** - should be CONFIRMED after successful payment
5. **Test refunds** if needed

## Error Handling

The mock system simulates common payment errors:
- Invalid card details
- Insufficient funds
- Network timeouts
- Gateway errors
- Declined transactions

## Webhook Simulation

A mock webhook endpoint is available for testing:
```bash
POST /api/v1/payments/webhook/mock
Content-Type: application/json

{
  "event": "charge.success",
  "data": {
    "reference": "MOCK_A1B2C3D4",
    "status": "success"
  }
}
```

This mock payment system provides all the functionality needed for development and testing while maintaining the same API structure you'll use with real payment gateways.