package com.example.demo.service.ebay;

import com.example.demo.model.dto.ListingResponse;
import com.example.demo.model.dto.VehicleSearchRequest;
import com.example.demo.model.entity.Listing;
import com.example.demo.repository.ListingRepository;
import com.example.demo.service.fitment.FitmentValidationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EbaySearchService {

    private final ListingRepository listingRepository;
    private final FitmentValidationService fitmentValidationService;

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

        saveOrUpdate(results, query, null);

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
                listingRepository
                        .findBySearchQueryAndSourceAndYearAndMakeAndModel(
                                query,
                                "EBAY",
                                request.getYear(),
                                request.getMake(),
                                request.getModel()
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

        // Compatible + incompatible mock listings
        List<ListingResponse> results = List.of(

                // Compatible
                ListingResponse.builder()
                        .title(
                                request.getYear() + " " +
                                        request.getMake() + " " +
                                        request.getModel() +
                                        " Brake Pads"
                        )
                        .source("EBAY")
                        .price(84.99)
                        .shippingCost(6.99)
                        .totalPrice(91.98)
                        .productUrl("https://www.ebay.com/mock-fitment-item-1")
                        .availability("In Stock")
                        .build(),

                // Incompatible make
                ListingResponse.builder()
                        .title("Ford F-150 Brake Pads")
                        .source("EBAY")
                        .price(72.50)
                        .shippingCost(5.00)
                        .totalPrice(77.50)
                        .productUrl("https://www.ebay.com/mock-fitment-item-2")
                        .availability("In Stock")
                        .build(),

                // Incompatible model
                ListingResponse.builder()
                        .title("Honda Civic Brake Pads")
                        .source("EBAY")
                        .price(65.00)
                        .shippingCost(4.99)
                        .totalPrice(69.99)
                        .productUrl("https://www.ebay.com/mock-fitment-item-3")
                        .availability("In Stock")
                        .build()
        );

        saveOrUpdate(results, query, request);

        List<Listing> fresh =
                listingRepository
                        .findBySearchQueryAndSourceAndYearAndMakeAndModel(
                                query,
                                "EBAY",
                                request.getYear(),
                                request.getMake(),
                                request.getModel()
                        );

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
     * Save or update cache
     */
    private void saveOrUpdate(
            List<ListingResponse> results,
            String query,
            VehicleSearchRequest vehicle
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

                        if (vehicle != null) {
                            existing.setYear(vehicle.getYear());
                            existing.setMake(vehicle.getMake());
                            existing.setModel(vehicle.getModel());

                            existing.setYearStart(2016);
                            existing.setYearEnd(2020);
                        }

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
                                .year(vehicle != null ? vehicle.getYear() : null)
                                .make(vehicle != null ? vehicle.getMake() : null)
                                .model(vehicle != null ? vehicle.getModel() : null)
                                .yearStart(2016)
                                .yearEnd(2020)
                                .build();

                        listingRepository.save(listing);
                    });
        });
    }

    /**
     * Entity → DTO
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