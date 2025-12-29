package com.hub.tofimotia.services;

import com.hub.tofimotia.models.User;
import com.hub.tofimotia.models.Venue;
import com.hub.tofimotia.repositories.UserRepository;
import com.hub.tofimotia.repositories.VenueRepository;
import com.hub.tofimotia.requests.VenueRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class VenueService {

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }

    public Optional<Venue> getVenueById(Long id) {
        return venueRepository.findById(id);
    }

    public List<Venue> searchVenues(String location, Integer minCapacity, Integer maxCapacity, 
                                   BigDecimal minPrice, BigDecimal maxPrice) {
        return venueRepository.findVenuesWithFilters(location, minCapacity, maxCapacity, minPrice, maxPrice);
    }

    public Venue createVenue(VenueRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        User vendor = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Venue venue = new Venue();
        venue.setVendor(vendor);
        venue.setName(request.getName());
        venue.setLocation(request.getLocation());
        venue.setCapacity(request.getCapacity());
        venue.setPricePerHour(request.getPricePerHour());
        venue.setAmenities(request.getAmenities());
        venue.setAvailability(request.getAvailability());
        venue.setImages(request.getImages());

        return venueRepository.save(venue);
    }

    public Venue updateVenue(Long id, VenueRequest request) {
        Venue venue = venueRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Venue not found"));

        // Check if current user is the owner
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!venue.getVendor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not authorized to update this venue");
        }

        venue.setName(request.getName());
        venue.setLocation(request.getLocation());
        venue.setCapacity(request.getCapacity());
        venue.setPricePerHour(request.getPricePerHour());
        venue.setAmenities(request.getAmenities());
        venue.setAvailability(request.getAvailability());
        venue.setImages(request.getImages());

        return venueRepository.save(venue);
    }

    public void deleteVenue(Long id) {
        Venue venue = venueRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Venue not found"));

        // Check if current user is the owner
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!venue.getVendor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not authorized to delete this venue");
        }

        venueRepository.delete(venue);
    }

    public List<Venue> getVenuesByVendor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User vendor = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return venueRepository.findByVendorId(vendor.getId());
    }
}