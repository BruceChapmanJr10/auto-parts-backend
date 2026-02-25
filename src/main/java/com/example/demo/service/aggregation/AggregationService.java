package com.example.demo.service.aggregation;


import com.example.demo.model.dto.ListingResponse;
import com.example.demo.service.amazon.AmazonSearchService;
import com.example.demo.service.ebay.EbaySearchService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AggregationService {

    private final EbaySearchService ebaySearchService;
    private final AmazonSearchService amazonSearchService;

    public AggregationService(
            EbaySearchService ebaySearchService,
            AmazonSearchService amazonSearchService
    ) {
        this.ebaySearchService = ebaySearchService;
        this.amazonSearchService = amazonSearchService;
    }

    public List<ListingResponse> searchAllSources(String query) {

        List<ListingResponse> results = new ArrayList<>();

        // Aggregate results from each provider
        results.addAll(ebaySearchService.search(query));
        results.addAll(amazonSearchService.search(query));

        return results;
    }
}
