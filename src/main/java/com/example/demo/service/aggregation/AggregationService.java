package com.example.demo.service.aggregation;


import com.example.demo.model.dto.ListingResponse;
import com.example.demo.service.ebay.EbaySearchService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AggregationService {

    private final EbaySearchService ebaySearchService;

    public AggregationService(EbaySearchService ebaySearchService) {
        this.ebaySearchService = ebaySearchService;
    }

    public List<ListingResponse> searchAllSources(String query) {
        return ebaySearchService.search(query);
    }
}
