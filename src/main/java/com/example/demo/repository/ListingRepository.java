package com.example.demo.repository;

import com.example.demo.model.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {

    List<Listing> findByPartNumber(String partNumber);

    List<Listing> findBySource(String source);
}
