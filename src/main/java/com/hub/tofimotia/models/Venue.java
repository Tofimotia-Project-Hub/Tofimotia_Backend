package com.hub.tofimotia.models;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "venues")
public class Venue extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User vendor;

    @Column(name = "name")
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "price_per_hour")
    private BigDecimal pricePerHour;

    @Type(JsonType.class)
    @Column(name = "amenities", columnDefinition = "jsonb")
    private Map<String,Object> amenities;

    @Type(JsonType.class)
    @Column(name = "availability", columnDefinition = "jsonb")
    private Map<String,Object> availability;

    @Type(JsonType.class)
    @Column(name = "images", columnDefinition = "jsonb")
    private List<String> images;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
