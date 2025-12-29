#!/bin/bash

# Tofimotia API Test Script
BASE_URL="http://localhost:8080/api/v1"

echo "🚀 Testing Tofimotia API..."

# Test 1: Register a vendor
echo "📝 1. Registering vendor..."
VENDOR_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "vendor@test.com",
    "password": "password123",
    "role": "VENDOR"
  }')

VENDOR_TOKEN=$(echo $VENDOR_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "✅ Vendor registered. Token: ${VENDOR_TOKEN:0:20}..."

# Test 2: Register a user
echo "📝 2. Registering user..."
USER_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@test.com",
    "password": "password123",
    "role": "USER"
  }')

USER_TOKEN=$(echo $USER_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "✅ User registered. Token: ${USER_TOKEN:0:20}..."

# Test 3: Create a venue
echo "🏢 3. Creating venue..."
VENUE_RESPONSE=$(curl -s -X POST "$BASE_URL/venues" \
  -H "Authorization: Bearer $VENDOR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Event Hall",
    "location": "Lagos, Nigeria",
    "capacity": 100,
    "pricePerHour": 5000,
    "amenities": {"wifi": true, "parking": true, "ac": true},
    "availability": {"monday": "9:00-18:00", "tuesday": "9:00-18:00"}
  }')

VENUE_ID=$(echo $VENUE_RESPONSE | grep -o '"id":[0-9]*' | cut -d':' -f2)
echo "✅ Venue created with ID: $VENUE_ID"

# Test 4: Search venues
echo "🔍 4. Searching venues..."
curl -s "$BASE_URL/venues/search?location=Lagos&minCapacity=50" | head -c 200
echo "..."
echo "✅ Search completed"

# Test 5: Check availability
echo "📅 5. Checking availability..."
curl -s "$BASE_URL/bookings/availability/$VENUE_ID?startTime=2024-02-01T10:00:00&endTime=2024-02-01T14:00:00" \
  -H "Authorization: Bearer $USER_TOKEN" | head -c 200
echo "..."
echo "✅ Availability checked"

# Test 6: Create booking
echo "📋 6. Creating booking..."
BOOKING_RESPONSE=$(curl -s -X POST "$BASE_URL/bookings" \
  -H "Authorization: Bearer $USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "venueId": '$VENUE_ID',
    "startDateTime": "2024-02-01T10:00:00",
    "endDateTime": "2024-02-01T14:00:00"
  }')

echo $BOOKING_RESPONSE | head -c 200
echo "..."
echo "✅ Booking created"

echo ""
echo "🎉 All tests completed! Check the responses above for details."
echo "📖 Visit http://localhost:8080/swagger-ui.html for interactive API documentation"