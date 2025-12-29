package com.hub.tofimotia.controllers;

import com.hub.tofimotia.models.Venue;
import com.hub.tofimotia.requests.VenueRequest;
import com.hub.tofimotia.responses.ApiResponse;
import com.hub.tofimotia.services.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/venues")
@Tag(name = "Venues", description = "Venue management APIs")
public class VenueController {

    @Autowired
    private VenueService venueService;

    @GetMapping
    @Operation(summary = "Get all venues")
    public ResponseEntity<ApiResponse<List<Venue>>> getAllVenues() {
        List<Venue> venues = venueService.getAllVenues();
        return ResponseEntity.ok(ApiResponse.success("Venues retrieved successfully", venues));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get venue by ID")
    public ResponseEntity<ApiResponse<Venue>> getVenueById(@PathVariable Long id) {
        Optional<Venue> venue = venueService.getVenueById(id);
        if (venue.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("Venue found", venue.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search venues with filters")
    public ResponseEntity<ApiResponse<List<Venue>>> searchVenues(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) Integer maxCapacity,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        
        List<Venue> venues = venueService.searchVenues(location, minCapacity, maxCapacity, minPrice, maxPrice);
        return ResponseEntity.ok(ApiResponse.success("Search completed", venues));
    }

    @PostMapping
    @Operation(summary = "Create a new venue", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Venue>> createVenue(@Valid @RequestBody VenueRequest request) {
        try {
            Venue venue = venueService.createVenue(request);
            return ResponseEntity.ok(ApiResponse.success("Venue created successfully", venue));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update venue", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Venue>> updateVenue(@PathVariable Long id, @Valid @RequestBody VenueRequest request) {
        try {
            Venue venue = venueService.updateVenue(id, request);
            return ResponseEntity.ok(ApiResponse.success("Venue updated successfully", venue));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete venue", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteVenue(@PathVariable Long id) {
        try {
            venueService.deleteVenue(id);
            return ResponseEntity.ok(ApiResponse.success("Venue deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my-venues")
    @Operation(summary = "Get venues by current vendor", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Venue>>> getMyVenues() {
        try {
            List<Venue> venues = venueService.getVenuesByVendor();
            return ResponseEntity.ok(ApiResponse.success("Venues retrieved successfully", venues));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}