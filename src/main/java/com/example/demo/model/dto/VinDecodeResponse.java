package com.example.demo.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VinDecodeResponse {

    private String vin;
    private Integer year;
    private String make;
    private String model;
}