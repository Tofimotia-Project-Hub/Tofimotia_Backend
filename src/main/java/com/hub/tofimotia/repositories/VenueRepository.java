package com.hub.tofimotia.repositories;

import com.hub.tofimotia.models.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    List<Venue> findByVendorId(Long vendorId);
    
    @Query("SELECT v FROM Venue v WHERE " +
           "(:location IS NULL OR LOWER(v.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:minCapacity IS NULL OR v.capacity >= :minCapacity) AND " +
           "(:maxCapacity IS NULL OR v.capacity <= :maxCapacity) AND " +
           "(:minPrice IS NULL OR v.pricePerHour >= :minPrice) AND " +
           "(:maxPrice IS NULL OR v.pricePerHour <= :maxPrice)")
    List<Venue> findVenuesWithFilters(
        @Param("location") String location,
        @Param("minCapacity") Integer minCapacity,
        @Param("maxCapacity") Integer maxCapacity,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice
    );
}