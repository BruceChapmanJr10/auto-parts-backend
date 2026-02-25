package com.example.demo.service.amazon;

import com.example.demo.model.dto.ListingResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AmazonSearchService {

    public List<ListingResponse> search(String query) {

        // Mock data until Amazon API integration
        return List.of(

                ListingResponse.builder()
                        .title("Brake Pads - Honda Accord (Amazon)")
                        .source("AMAZON")
                        .price(82.50)
                        .shippingCost(0.0)
                        .totalPrice(82.50)
                        .productUrl("https://www.amazon.com/mock-item-1")
                        .availability("In Stock")
                        .build()
        );
    }
}
