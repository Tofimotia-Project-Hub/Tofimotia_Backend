package com.hub.tofimotia.services;

import com.hub.tofimotia.models.Bookings;
import com.hub.tofimotia.models.BookingStatus;
import com.hub.tofimotia.models.User;
import com.hub.tofimotia.models.Venue;
import com.hub.tofimotia.repositories.BookingRepository;
import com.hub.tofimotia.repositories.UserRepository;
import com.hub.tofimotia.repositories.VenueRepository;
import com.hub.tofimotia.requests.BookingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean isVenueAvailable(Long venueId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Bookings> conflictingBookings = bookingRepository.findConflictingBookings(venueId, startTime, endTime);
        return conflictingBookings.isEmpty();
    }

    public Bookings createBooking(BookingRequest request) {
        // Validate booking times
        if (request.getEndDateTime().isBefore(request.getStartDateTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        // Check venue availability
        if (!isVenueAvailable(request.getVenueId(), request.getStartDateTime(), request.getEndDateTime())) {
            throw new RuntimeException("Venue is not available for the selected time slot");
        }

        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User customer = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Get venue
        Venue venue = venueRepository.findById(request.getVenueId())
            .orElseThrow(() -> new RuntimeException("Venue not found"));

        // Calculate total amount
        Duration duration = Duration.between(request.getStartDateTime(), request.getEndDateTime());
        long hours = duration.toHours();
        if (duration.toMinutes() % 60 > 0) {
            hours++; // Round up partial hours
        }
        BigDecimal totalAmount = venue.getPricePerHour().multiply(BigDecimal.valueOf(hours));

        // Create booking
        Bookings booking = new Bookings();
        booking.setVenue(venue);
        booking.setCustomer(customer);
        booking.setStartDateTime(request.getStartDateTime());
        booking.setEndDateTime(request.getEndDateTime());
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalAmount(totalAmount);
        booking.setPaymentStatus("PENDING");

        return bookingRepository.save(booking);
    }

    public List<Bookings> getUserBookings() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return bookingRepository.findByCustomerId(user.getId());
    }

    public List<Bookings> getVenueBookings(Long venueId) {
        // Verify venue ownership
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User vendor = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Venue venue = venueRepository.findById(venueId)
            .orElseThrow(() -> new RuntimeException("Venue not found"));

        if (!venue.getVendor().getId().equals(vendor.getId())) {
            throw new RuntimeException("Not authorized to view bookings for this venue");
        }

        return bookingRepository.findByVenueId(venueId);
    }

    public Optional<Bookings> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public Bookings updateBookingStatus(Long id, BookingStatus status) {
        Bookings booking = bookingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(status);
        return bookingRepository.save(booking);
    }
}