package com.example.demo.service.ebay;


import com.example.demo.model.dto.ListingResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EbaySearchService {

    public List<ListingResponse> search(String query) {

        return List.of(

                ListingResponse.builder()
                        .title("Brake Pads - Honda Accord")
                        .source("EBAY")
                        .price(79.99)
                        .shippingCost(5.99)
                        .totalPrice(85.98)
                        .productUrl("https://www.ebay.com")
                        .availability("In Stock")
                        .build(),

                ListingResponse.builder()
                        .title("Premium Brake Pads Set")
                        .source("EBAY")
                        .price(69.99)
                        .shippingCost(0.0)
                        .totalPrice(69.99)
                        .productUrl("https://www.ebay.com")
                        .availability("In Stock")
                        .build()
        );
    }
}
