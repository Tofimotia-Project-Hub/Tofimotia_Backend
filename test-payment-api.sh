#!/bin/bash

# Test Payment API Script
# Replace BASE_URL with your deployed URL
BASE_URL="https://your-deployed-app.com"
# BASE_URL="http://localhost:8080"

echo "🚀 Testing Tofimotia Payment API"
echo "================================"

# Step 1: Register a test user
echo "1. Registering test user..."
REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/api/v1/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@example.com",
    "password": "password123",
    "role": "USER"
  }')

echo "Register Response: $REGISTER_RESPONSE"

# Step 2: Login to get JWT token
echo -e "\n2. Logging in..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@example.com",
    "password": "password123"
  }')

echo "Login Response: $LOGIN_RESPONSE"

# Extract JWT token (you might need to adjust this based on your response format)
JWT_TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "JWT Token: $JWT_TOKEN"

if [ -z "$JWT_TOKEN" ]; then
    echo "❌ Failed to get JWT token. Please check login response."
    exit 1
fi

# Step 3: Create a test venue (if you're a vendor)
echo -e "\n3. Creating test venue..."
VENUE_RESPONSE=$(curl -s -X POST "$BASE_URL/api/v1/venues" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Event Hall",
    "location": "Lagos, Nigeria",
    "capacity": 100,
    "pricePerHour": 50000,
    "amenities": ["WiFi", "Parking", "AC"],
    "description": "Beautiful event hall for testing"
  }')

echo "Venue Response: $VENUE_RESPONSE"

# Step 4: Create a test booking
echo -e "\n4. Creating test booking..."
BOOKING_RESPONSE=$(curl -s -X POST "$BASE_URL/api/v1/bookings" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "venueId": 1,
    "startDateTime": "2026-03-15T10:00:00",
    "endDateTime": "2026-03-15T14:00:00"
  }')

echo "Booking Response: $BOOKING_RESPONSE"

# Step 5: Test Payment Initiation
echo -e "\n5. Initiating payment..."
PAYMENT_INIT_RESPONSE=$(curl -s -X POST "$BASE_URL/api/v1/payments/initiate" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "bookingId": 1,
    "amount": 200000.00,
    "paymentMethod": "MOCK_PAYMENT",
    "cardNumber": "4084084084084081",
    "expiryMonth": "12",
    "expiryYear": "2025",
    "cvv": "123",
    "cardHolderName": "Test User",
    "customerEmail": "testuser@example.com",
    "customerPhone": "+2348012345678"
  }')

echo "Payment Init Response: $PAYMENT_INIT_RESPONSE"

# Extract payment reference
PAYMENT_REF=$(echo $PAYMENT_INIT_RESPONSE | grep -o '"reference":"[^"]*' | cut -d'"' -f4)
echo "Payment Reference: $PAYMENT_REF"

if [ -z "$PAYMENT_REF" ]; then
    echo "❌ Failed to get payment reference. Please check payment init response."
    exit 1
fi

# Step 6: Verify Payment
echo -e "\n6. Verifying payment..."
sleep 2  # Wait a bit before verification
PAYMENT_VERIFY_RESPONSE=$(curl -s -X GET "$BASE_URL/api/v1/payments/verify/$PAYMENT_REF" \
  -H "Authorization: Bearer $JWT_TOKEN")

echo "Payment Verify Response: $PAYMENT_VERIFY_RESPONSE"

# Step 7: Get Payment History
echo -e "\n7. Getting payment history..."
PAYMENT_HISTORY_RESPONSE=$(curl -s -X GET "$BASE_URL/api/v1/payments/my-payments" \
  -H "Authorization: Bearer $JWT_TOKEN")

echo "Payment History Response: $PAYMENT_HISTORY_RESPONSE"

# Step 8: Test different card scenarios
echo -e "\n8. Testing failure scenario (card ending in 0000)..."
PAYMENT_FAIL_RESPONSE=$(curl -s -X POST "$BASE_URL/api/v1/payments/initiate" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "bookingId": 1,
    "amount": 100000.00,
    "paymentMethod": "MOCK_PAYMENT",
    "cardNumber": "4084084084084000",
    "expiryMonth": "12",
    "expiryYear": "2025",
    "cvv": "123",
    "cardHolderName": "Test User",
    "customerEmail": "testuser@example.com"
  }')

echo "Payment Fail Test Response: $PAYMENT_FAIL_RESPONSE"

echo -e "\n✅ Payment API testing completed!"
echo "Check the responses above to verify everything is working correctly."