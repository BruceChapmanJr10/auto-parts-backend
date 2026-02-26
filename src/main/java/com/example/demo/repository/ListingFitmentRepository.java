package com.example.demo.repository;

import com.example.demo.model.entity.ListingFitment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingFitmentRepository
        extends JpaRepository<ListingFitment, Long> {
}
