package com.hub.tofimotia.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class VenueRequest {
    @NotBlank(message = "Venue name is required")
    private String name;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be positive")
    private Integer capacity;

    @NotNull(message = "Price per hour is required")
    @Positive(message = "Price must be positive")
    private BigDecimal pricePerHour;

    private Map<String, Object> amenities;
    private Map<String, Object> availability;
    private List<String> images;
}