package com.example.demo.service.garage;

import com.example.demo.model.entity.GarageVehicle;
import com.example.demo.repository.GarageVehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GarageService {

    private final GarageVehicleRepository repository;

    public GarageService(GarageVehicleRepository repository) {
        this.repository = repository;
    }

    // Save vehicle to garage
    public GarageVehicle saveVehicle(GarageVehicle vehicle) {

        vehicle.setCreatedAt(System.currentTimeMillis());

        return repository.save(vehicle);
    }

    // Get all vehicles in a garage
    public List<GarageVehicle> getGarage(String garageId) {
        return repository.findByGarageId(garageId);
    }
}
