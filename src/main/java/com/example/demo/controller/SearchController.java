package com.example.demo.controller;

import com.example.demo.model.dto.GarageSearchRequest;
import com.example.demo.model.dto.ListingResponse;
import com.example.demo.model.dto.VehicleSearchRequest;
import com.example.demo.service.aggregation.AggregationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin
public class SearchController {
    private final AggregationService aggregationService;

    public SearchController(
            AggregationService aggregationService
    ) {
        this.aggregationService = aggregationService;
    }

    /**
     * Vehicle fitment search
     */
    @PostMapping
    public List<ListingResponse> searchVehicle(
            @RequestBody VehicleSearchRequest request
    ) {
        return aggregationService.searchVehicle(request);
    }

    /**
     * Keyword fallback search
     */
    @GetMapping
    public List<ListingResponse> searchKeyword(
            @RequestParam String query
    ) {
        return aggregationService.searchAllSources(query);
    }

    /**
     * Garage-based search
     */
    @PostMapping("/garage")
    public List<ListingResponse> searchGarage(
            @RequestBody GarageSearchRequest request
    ) {
        return aggregationService.searchByGarage(request);
    }
}
