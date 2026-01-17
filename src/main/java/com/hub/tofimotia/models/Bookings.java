package com.hub.tofimotia.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "bookings")
public class Bookings extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User customer;

    @Column(name = "start_time")
    private LocalDateTime startDateTime;
    
    @Column(name = "end_time")
    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;
    
    @Column(name = "special_requests")
    private String specialRequests;
}
