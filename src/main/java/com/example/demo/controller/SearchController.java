package com.example.demo.controller;

import com.example.demo.model.dto.ListingResponse;
import com.example.demo.service.aggregation.AggregationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin
public class SearchController {
    private final AggregationService aggregationService;

    public SearchController(AggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    @GetMapping
    public List<ListingResponse> search(@RequestParam String query) {
        return aggregationService.searchAllSources(query);
    }


}
