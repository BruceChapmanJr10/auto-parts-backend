package com.example.demo.service.ebay;


import com.example.demo.model.dto.ListingResponse;
import com.example.demo.model.entity.Listing;
import com.example.demo.repository.ListingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EbaySearchService {

    private final ListingRepository listingRepository;

    // 6-hour cache window (milliseconds)
    private static final long CACHE_EXPIRATION_MS = 21600000;

    public EbaySearchService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    public List<ListingResponse> search(String query) {

        long now = System.currentTimeMillis();

        // --------------------------------------------
        // 1. Check cached listings for this query
        // --------------------------------------------
        List<Listing> cachedListings =
                listingRepository.findBySearchQueryAndSource(query, "EBAY");

        boolean cacheValid = !cachedListings.isEmpty() &&
                cachedListings.stream()
                        .allMatch(l -> (now - l.getLastUpdated()) < CACHE_EXPIRATION_MS);

        if (cacheValid) {
            // Return fresh cached results
            return cachedListings.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }

        // --------------------------------------------
        // 2. Fetch fresh results (mock for now)
        // --------------------------------------------
        List<ListingResponse> results = List.of(

                ListingResponse.builder()
                        .title("Brake Pads - Honda Accord")
                        .source("EBAY")
                        .price(79.99)
                        .shippingCost(5.99)
                        .totalPrice(85.98)
                        .productUrl("https://www.ebay.com/mock-item-1")
                        .availability("In Stock")
                        .build()
        );

        // --------------------------------------------
        // 3. Save/update cache
        // --------------------------------------------
        results.forEach(r -> {

            listingRepository.findByProductUrl(r.getProductUrl())
                    .ifPresentOrElse(existing -> {

                        // Update existing listing
                        existing.setPrice(r.getPrice());
                        existing.setShippingCost(r.getShippingCost());
                        existing.setTotalPrice(r.getTotalPrice());
                        existing.setAvailability(r.getAvailability());
                        existing.setLastUpdated(now);
                        existing.setSearchQuery(query);

                        listingRepository.save(existing);

                    }, () -> {

                        // Insert new listing
                        Listing listing = Listing.builder()
                                .searchQuery(query)
                                .title(r.getTitle())
                                .source(r.getSource())
                                .price(r.getPrice())
                                .shippingCost(r.getShippingCost())
                                .totalPrice(r.getTotalPrice())
                                .productUrl(r.getProductUrl())
                                .availability(r.getAvailability())
                                .lastUpdated(now)
                                .build();

                        listingRepository.save(listing);
                    });
        });

        return results;
    }

    // Entity → DTO mapping
    private ListingResponse mapToResponse(Listing listing) {

        return ListingResponse.builder()
                .title(listing.getTitle())
                .source(listing.getSource())
                .price(listing.getPrice())
                .shippingCost(listing.getShippingCost())
                .totalPrice(listing.getTotalPrice())
                .productUrl(listing.getProductUrl())
                .availability(listing.getAvailability())
                .build();
    }
      
}
