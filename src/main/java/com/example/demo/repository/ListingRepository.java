package com.example.demo.repository;

import com.example.demo.model.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ListingRepository extends JpaRepository<Listing, Long> {

    // Used for query-based caching
    List<Listing> findBySearchQueryAndSource(String searchQuery, String source);

    // Used to prevent duplicate inserts
    Optional<Listing> findByProductUrl(String productUrl);
}
