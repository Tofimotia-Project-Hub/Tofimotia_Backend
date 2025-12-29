package com.hub.tofimotia.repositories;

import com.hub.tofimotia.models.Bookings;
import com.hub.tofimotia.models.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Bookings, Long> {
    List<Bookings> findByCustomerId(Long customerId);
    List<Bookings> findByVenueId(Long venueId);
    List<Bookings> findByStatus(BookingStatus status);
    
    @Query("SELECT b FROM Bookings b WHERE b.venue.id = :venueId AND " +
           "b.status IN ('CONFIRMED', 'PENDING') AND " +
           "((b.startDateTime <= :endTime AND b.endDateTime >= :startTime))")
    List<Bookings> findConflictingBookings(
        @Param("venueId") Long venueId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
}