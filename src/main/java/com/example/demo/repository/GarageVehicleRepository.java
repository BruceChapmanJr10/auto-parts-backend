package com.example.demo.repository;

import com.example.demo.model.entity.GarageVehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GarageVehicleRepository
        extends JpaRepository<GarageVehicle, Long> {

    List<GarageVehicle> findByGarageId(String garageId);
}
