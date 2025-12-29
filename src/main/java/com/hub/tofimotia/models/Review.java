package com.hub.tofimotia.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "reviews")
public class Review extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "booking_id")
    private Bookings booking;

   private int rating;
   private String comment;
}
