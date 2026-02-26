package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Search term used to fetch this listing (enables query-based caching)
    @Column(nullable = false)
    private String searchQuery;

    private String title;

    private String source; // EBAY, AMAZON (future)

    private Double price;

    private Double shippingCost;

    private Double totalPrice;

    @Column(unique = true)
    private String productUrl;

    private String availability;

    // Timestamp used for 6-hour cache expiration
    private Long lastUpdated;

    // Vehicle fitment fields
    private Integer year;

    private String make;

    private String model;

    // Fitment validation fields

    @Column(name = "year_start")
    private Integer yearStart;

    @Column(name = "year_end")
    private Integer yearEnd;
}
