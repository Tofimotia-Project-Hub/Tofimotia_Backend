package com.hub.tofimotia.repositories;

import com.hub.tofimotia.enums.PaymentStatus;
import com.hub.tofimotia.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByReference(String reference);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    List<Payment> findByBookingId(Long bookingId);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.booking.customer.id = :userId ORDER BY p.createdAt DESC")
    List<Payment> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT p FROM Payment p WHERE p.booking.venue.vendor.id = :vendorId ORDER BY p.createdAt DESC")
    List<Payment> findByVendorId(@Param("vendorId") Long vendorId);
}