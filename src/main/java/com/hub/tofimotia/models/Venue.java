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
    @JoinColumn(name = "vendor_id",nullable = false)
    private User vendor;

    private String name;
    private String location;
    private int capacity;

    @Column(name ="price_per_hour")
    private BigDecimal pricePerHour;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String,Object> amenities;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String,Object> availability;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<String> images;

}
