package com.example.demo.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListingResponse {

    private String title;
    private String source;
    private Double price;
    private Double shippingCost;
    private Double totalPrice;
    private String productUrl;
    private String availability;
}
