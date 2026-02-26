package com.example.demo.service.aggregation;


import com.example.demo.model.dto.GarageSearchRequest;
import com.example.demo.model.dto.ListingResponse;
import com.example.demo.model.dto.VehicleSearchRequest;
import com.example.demo.model.entity.GarageVehicle;
import com.example.demo.repository.GarageVehicleRepository;
import com.example.demo.service.affiliate.AffiliateLinkService;
import com.example.demo.service.amazon.AmazonSearchService;
import com.example.demo.service.ebay.EbaySearchService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AggregationService {
    private final EbaySearchService ebaySearchService;
    private final AmazonSearchService amazonSearchService;
    private final AffiliateLinkService affiliateLinkService;
    private final GarageVehicleRepository garageVehicleRepository;

    public AggregationService(
            EbaySearchService ebaySearchService,
            AmazonSearchService amazonSearchService,
            AffiliateLinkService affiliateLinkService,
            GarageVehicleRepository garageVehicleRepository
    ) {
        this.ebaySearchService = ebaySearchService;
        this.amazonSearchService = amazonSearchService;
        this.affiliateLinkService = affiliateLinkService;
        this.garageVehicleRepository = garageVehicleRepository;
    }

    /**
     * Keyword search
     */
    public List<ListingResponse> searchAllSources(String query) {

        List<ListingResponse> results = new ArrayList<>();

        results.addAll(ebaySearchService.search(query));
        results.addAll(amazonSearchService.search(query));

        results.replaceAll(affiliateLinkService::inject);

        results.sort(
                Comparator.comparing(ListingResponse::getTotalPrice)
        );

        return results;
    }

    /**
     * Single vehicle search
     */
    public List<ListingResponse> searchVehicle(
            VehicleSearchRequest request
    ) {

        String query =
                request.getYear() + " " +
                        request.getMake() + " " +
                        request.getModel() + " " +
                        request.getPart();

        List<ListingResponse> results = new ArrayList<>();

        results.addAll(
                ebaySearchService.searchWithVehicle(
                        request,
                        query
                )
        );

        results.addAll(
                amazonSearchService.search(query)
        );

        results.replaceAll(affiliateLinkService::inject);

        results.sort(
                Comparator.comparing(ListingResponse::getTotalPrice)
        );

        return results;
    }

    /**
     * Multi-vehicle garage search
     */
    public List<ListingResponse> searchByGarage(
            GarageSearchRequest request
    ) {

        List<GarageVehicle> vehicles =
                garageVehicleRepository
                        .findByGarageId(request.getGarageId());

        if (vehicles.isEmpty()) {
            throw new RuntimeException(
                    "Garage has no saved vehicles"
            );
        }

        List<ListingResponse> allResults =
                new ArrayList<>();

        // Run search for each vehicle
        for (GarageVehicle vehicle : vehicles) {

            VehicleSearchRequest vehicleRequest =
                    new VehicleSearchRequest();

            vehicleRequest.setYear(vehicle.getYear());
            vehicleRequest.setMake(vehicle.getMake());
            vehicleRequest.setModel(vehicle.getModel());
            vehicleRequest.setPart(request.getPart());

            allResults.addAll(
                    searchVehicle(vehicleRequest)
            );
        }

        // Remove duplicates by product URL
        Map<String, ListingResponse> unique =
                allResults.stream()
                        .collect(Collectors.toMap(
                                ListingResponse::getProductUrl,
                                r -> r,
                                (a, b) -> a
                        ));

        List<ListingResponse> deduped =
                new ArrayList<>(unique.values());

        // Inject affiliate links
        deduped.replaceAll(affiliateLinkService::inject);

        // Sort by lowest price
        deduped.sort(
                Comparator.comparing(
                        ListingResponse::getTotalPrice
                )
        );

        return deduped;
    }
}