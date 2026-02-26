package com.example.demo.service.vin;

import com.example.demo.model.dto.VinDecodeResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class VinService {

    private final RestTemplate restTemplate;

    public VinService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public VinDecodeResponse decode(String vin) {

        String url =
                "https://vpic.nhtsa.dot.gov/api/vehicles/DecodeVin/"
                        + vin + "?format=json";

        Map<String, Object> response =
                restTemplate.getForObject(url, Map.class);

        List<Map<String, Object>> results =
                (List<Map<String, Object>>) response.get("Results");

        String make = null;
        String model = null;
        Integer year = null;

        for (Map<String, Object> item : results) {

            String variable =
                    (String) item.get("Variable");

            String value =
                    (String) item.get("Value");

            if (value == null) continue;

            switch (variable) {
                case "Make" -> make = value;
                case "Model" -> model = value;
                case "Model Year" -> {
                    try {
                        year = Integer.parseInt(value);
                    } catch (Exception ignored) {}
                }
            }
        }

        return VinDecodeResponse.builder()
                .vin(vin)
                .year(year)
                .make(make)
                .model(model)
                .build();
    }
}