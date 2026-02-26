package com.example.demo.controller;

import com.example.demo.model.entity.GarageVehicle;
import com.example.demo.service.garage.GarageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/garage")
@CrossOrigin
public class GarageController {

    private final GarageService garageService;

    public GarageController(GarageService garageService) {
        this.garageService = garageService;
    }

    // Save vehicle
    @PostMapping
    public GarageVehicle saveVehicle(
            @RequestBody GarageVehicle vehicle
    ) {
        return garageService.saveVehicle(vehicle);
    }

    // Get garage vehicles
    @GetMapping("/{garageId}")
    public List<GarageVehicle> getGarage(
            @PathVariable String garageId
    ) {
        return garageService.getGarage(garageId);
    }
}
