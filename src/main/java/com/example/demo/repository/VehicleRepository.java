package com.example.demo.repository;

import com.example.demo.model.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByYear(Integer year);

    List<Vehicle> findByYearAndMake(Integer year, String make);

}
