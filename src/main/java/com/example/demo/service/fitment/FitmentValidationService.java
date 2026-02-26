package com.example.demo.service.fitment;

import com.example.demo.model.dto.VehicleSearchRequest;
import com.example.demo.model.entity.Listing;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FitmentValidationService {

    /**
     * Filters listings by vehicle compatibility
     */
    public List<Listing> validateFitment(
            List<Listing> listings,
            VehicleSearchRequest vehicle
    ) {

        return listings.stream()
                .filter(l -> isCompatible(l, vehicle))
                .collect(Collectors.toList());
    }

    /**
     * Compatibility logic
     */
    private boolean isCompatible(
            Listing listing,
            VehicleSearchRequest vehicle
    ) {

        // Exact make/model match
        if (listing.getMake() != null &&
                !listing.getMake().equalsIgnoreCase(vehicle.getMake())) {
            return false;
        }

        if (listing.getModel() != null &&
                !listing.getModel().equalsIgnoreCase(vehicle.getModel())) {
            return false;
        }

        // Year range validation
        if (listing.getYearStart() != null &&
                vehicle.getYear() < listing.getYearStart()) {
            return false;
        }

        if (listing.getYearEnd() != null &&
                vehicle.getYear() > listing.getYearEnd()) {
            return false;
        }

        return true;
    }
}
