package com.example.demo.service.aggregation;


import com.example.demo.model.dto.ListingResponse;
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

    public List<ListingResponse> searchAllSources(String query) {

        List<ListingResponse> results = new ArrayList<>();

        // Aggregate provider results
        results.addAll(ebaySearchService.search(query));
        results.addAll(amazonSearchService.search(query));

        // Inject affiliate tracking links
        results.replaceAll(affiliateLinkService::inject);

        // Sort by lowest total price
        results.sort(Comparator.comparing(ListingResponse::getTotalPrice));

        return results;
    }
}
