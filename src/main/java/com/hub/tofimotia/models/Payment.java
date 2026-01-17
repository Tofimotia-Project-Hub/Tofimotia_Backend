package com.hub.tofimotia.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Bookings booking;

    @Column(name = "amount")
    private BigDecimal amount;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "transaction_id")
    private String transactionId;
}
