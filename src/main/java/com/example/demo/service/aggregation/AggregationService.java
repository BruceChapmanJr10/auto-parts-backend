package com.example.demo.service.aggregation;


import com.example.demo.model.dto.ListingResponse;
import com.example.demo.model.dto.VehicleSearchRequest;
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

    public AggregationService(
            EbaySearchService ebaySearchService,
            AmazonSearchService amazonSearchService,
            AffiliateLinkService affiliateLinkService
    ) {
        this.ebaySearchService = ebaySearchService;
        this.amazonSearchService = amazonSearchService;
        this.affiliateLinkService = affiliateLinkService;
    }

    /**
     * ============================================
     * KEYWORD SEARCH (Original Search)
     * ============================================
     * Aggregates listings from all providers
     * using a simple text query.
     */
    public List<ListingResponse> searchAllSources(String query) {

        List<ListingResponse> results = new ArrayList<>();

        // Pull results from each provider
        results.addAll(ebaySearchService.search(query));
        results.addAll(amazonSearchService.search(query));

        // Inject affiliate tracking links
        results.replaceAll(affiliateLinkService::inject);

        // Sort by lowest total price
        results.sort(Comparator.comparing(ListingResponse::getTotalPrice));

        return results;
    }

    /**
     * ============================================
     * VEHICLE FITMENT SEARCH
     * ============================================
     * Builds a structured vehicle query and
     * searches providers using fitment data.
     */
    public List<ListingResponse> searchVehicle(
            VehicleSearchRequest request
    ) {
        List<ListingResponse> results = new ArrayList<>();

        // Build formatted search string
        // Example: "2018 Honda Accord brake pads"
        String formattedQuery =
                request.getYear() + " " +
                        request.getMake() + " " +
                        request.getModel() + " " +
                        request.getPart();

        // Provider searches
        results.addAll(
                ebaySearchService.searchWithVehicle(
                        request,
                        formattedQuery
                )
        );

        results.addAll(
                amazonSearchService.search(formattedQuery)
        );

        // Inject affiliate links
        results.replaceAll(affiliateLinkService::inject);

        // Sort by total price ascending
        results.sort(
                Comparator.comparing(
                        ListingResponse::getTotalPrice
                )
        );

        return results;
    }
}
