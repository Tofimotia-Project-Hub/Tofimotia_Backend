package com.hub.tofimotia.controllers;

import com.hub.tofimotia.models.Bookings;
import com.hub.tofimotia.models.BookingStatus;
import com.hub.tofimotia.requests.BookingRequest;
import com.hub.tofimotia.responses.ApiResponse;
import com.hub.tofimotia.services.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "Bookings", description = "Booking management APIs")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/availability/{venueId}")
    @Operation(summary = "Check venue availability")
    public ResponseEntity<ApiResponse<Boolean>> checkAvailability(
            @PathVariable Long venueId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        boolean available = bookingService.isVenueAvailable(venueId, startTime, endTime);
        return ResponseEntity.ok(ApiResponse.success("Availability checked", available));
    }

    @PostMapping
    @Operation(summary = "Create a new booking")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Bookings>> createBooking(@Valid @RequestBody BookingRequest request) {
        try {
            Bookings booking = bookingService.createBooking(request);
            return ResponseEntity.ok(ApiResponse.success("Booking created successfully", booking));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my-bookings")
    @Operation(summary = "Get current user's bookings")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Bookings>>> getMyBookings() {
        try {
            List<Bookings> bookings = bookingService.getUserBookings();
            return ResponseEntity.ok(ApiResponse.success("Bookings retrieved successfully", bookings));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/venue/{venueId}")
    @Operation(summary = "Get bookings for a specific venue")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Bookings>>> getVenueBookings(@PathVariable Long venueId) {
        try {
            List<Bookings> bookings = bookingService.getVenueBookings(venueId);
            return ResponseEntity.ok(ApiResponse.success("Venue bookings retrieved successfully", bookings));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID")
    public ResponseEntity<ApiResponse<Bookings>> getBookingById(@PathVariable Long id) {
        Optional<Bookings> booking = bookingService.getBookingById(id);
        if (booking.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("Booking found", booking.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update booking status")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Bookings>> updateBookingStatus(
            @PathVariable Long id, 
            @RequestParam BookingStatus status) {
        try {
            Bookings booking = bookingService.updateBookingStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("Booking status updated successfully", booking));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}