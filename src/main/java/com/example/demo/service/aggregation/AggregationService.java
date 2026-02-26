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
     * Standard keyword aggregation
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
     * Vehicle fitment aggregation
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
     * Garage-based search
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

        // For lightweight version we use first vehicle
        GarageVehicle vehicle = vehicles.get(0);

        VehicleSearchRequest vehicleRequest =
                new VehicleSearchRequest();

        vehicleRequest.setYear(vehicle.getYear());
        vehicleRequest.setMake(vehicle.getMake());
        vehicleRequest.setModel(vehicle.getModel());
        vehicleRequest.setPart(request.getPart());

        return searchVehicle(vehicleRequest);
    }
}
