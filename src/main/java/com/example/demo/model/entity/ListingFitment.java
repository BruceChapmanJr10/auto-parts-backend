package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "listing_fitment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingFitment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "listing_id")
    private Listing listing;

    private Integer yearStart;
    private Integer yearEnd;

    private String make;
    private String model;

    private String trim;
    private String engine;
}