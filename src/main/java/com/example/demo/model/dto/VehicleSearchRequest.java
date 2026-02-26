package com.example.demo.model.dto;

import lombok.Data;

@Data
public class VehicleSearchRequest {
    private Integer year;
    private String make;
    private String model;
    private String part;

}
