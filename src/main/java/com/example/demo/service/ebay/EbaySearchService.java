package com.example.demo.service.ebay;

import com.example.demo.model.dto.ListingResponse;
import com.example.demo.model.dto.VehicleSearchRequest;
import com.example.demo.model.entity.Listing;
import com.example.demo.model.entity.ListingFitment;
import com.example.demo.repository.ListingRepository;
import com.example.demo.service.fitment.FitmentValidationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EbaySearchService {

    private final ListingRepository listingRepository;
    private final FitmentValidationService fitmentValidationService;

    // 6 hour cache window
    private static final long CACHE_EXPIRATION_MS = 21600000;

    public EbaySearchService(
            ListingRepository listingRepository,
            FitmentValidationService fitmentValidationService
    ) {
        this.listingRepository = listingRepository;
        this.fitmentValidationService = fitmentValidationService;
    }

    /**
     * Keyword search
     */
    public List<ListingResponse> search(String query) {

        long now = System.currentTimeMillis();

        List<Listing> cached =
                listingRepository.findBySearchQueryAndSource(query, "EBAY");

        boolean cacheValid = !cached.isEmpty() &&
                cached.stream()
                        .allMatch(l ->
                                (now - l.getLastUpdated()) < CACHE_EXPIRATION_MS
                        );

        if (cacheValid) {
            return cached.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }

        // Mock keyword result
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

        saveOrUpdate(results, query);

        return results;
    }

    /**
     * Vehicle fitment search
     */
    public List<ListingResponse> searchWithVehicle(
            VehicleSearchRequest request,
            String query
    ) {

        long now = System.currentTimeMillis();

        List<Listing> cached =
                listingRepository.findBySearchQueryAndSource(
                        query,
                        "EBAY"
                );

        boolean cacheValid = !cached.isEmpty() &&
                cached.stream()
                        .allMatch(l ->
                                (now - l.getLastUpdated()) < CACHE_EXPIRATION_MS
                        );

        if (cacheValid) {

            List<Listing> validated =
                    fitmentValidationService.validateFitment(
                            cached,
                            request
                    );

            return validated.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }

        // -----------------------------
        // Mock multi-fitment listings
        // -----------------------------

        List<ListingResponse> results = List.of(

                // Compatible listing
                ListingResponse.builder()
                        .title("Honda Accord Brake Pads OEM")
                        .source("EBAY")
                        .price(84.99)
                        .shippingCost(6.99)
                        .totalPrice(91.98)
                        .productUrl("https://www.ebay.com/mock-fitment-1")
                        .availability("In Stock")
                        .build(),

                // Compatible listing
                ListingResponse.builder()
                        .title("Premium Ceramic Brake Pads Kit")
                        .source("EBAY")
                        .price(99.99)
                        .shippingCost(0.00)
                        .totalPrice(99.99)
                        .productUrl("https://www.ebay.com/mock-fitment-2")
                        .availability("In Stock")
                        .build(),

                // Incompatible listing
                ListingResponse.builder()
                        .title("Ford F-150 Heavy Duty Brake Pads")
                        .source("EBAY")
                        .price(129.99)
                        .shippingCost(10.99)
                        .totalPrice(140.98)
                        .productUrl("https://www.ebay.com/mock-fitment-3")
                        .availability("In Stock")
                        .build()
        );

        saveOrUpdate(results, query);

        // Reload saved listings
        List<Listing> fresh =
                listingRepository.findBySearchQueryAndSource(
                        query,
                        "EBAY"
                );

        // Apply fitment validation
        List<Listing> validated =
                fitmentValidationService.validateFitment(
                        fresh,
                        request
                );

        return validated.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Save or update cached listings
     */
    private void saveOrUpdate(
            List<ListingResponse> results,
            String query
    ) {

        long now = System.currentTimeMillis();

        results.forEach(r -> {

            listingRepository.findByProductUrl(r.getProductUrl())
                    .ifPresentOrElse(existing -> {

                        existing.setPrice(r.getPrice());
                        existing.setShippingCost(r.getShippingCost());
                        existing.setTotalPrice(r.getTotalPrice());
                        existing.setAvailability(r.getAvailability());
                        existing.setLastUpdated(now);
                        existing.setSearchQuery(query);

                        listingRepository.save(existing);

                    }, () -> {

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

                        // -----------------------------
                        // Multi-vehicle fitment mock
                        // -----------------------------

                        ListingFitment accordFitment =
                                ListingFitment.builder()
                                        .listing(listing)
                                        .make("Honda")
                                        .model("Accord")
                                        .yearStart(2013)
                                        .yearEnd(2022)
                                        .build();

                        ListingFitment civicFitment =
                                ListingFitment.builder()
                                        .listing(listing)
                                        .make("Honda")
                                        .model("Civic")
                                        .yearStart(2016)
                                        .yearEnd(2021)
                                        .build();

                        // Incompatible fitment
                        ListingFitment f150Fitment =
                                ListingFitment.builder()
                                        .listing(listing)
                                        .make("Ford")
                                        .model("F-150")
                                        .yearStart(2015)
                                        .yearEnd(2024)
                                        .build();

                        listing.setFitments(
                                List.of(
                                        accordFitment,
                                        civicFitment,
                                        f150Fitment
                                )
                        );

                        listingRepository.save(listing);
                    });
        });
    }

    /**
     * Entity → DTO mapping
     */
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