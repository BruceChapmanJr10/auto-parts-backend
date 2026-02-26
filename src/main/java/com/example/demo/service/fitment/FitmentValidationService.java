package com.example.demo.service.fitment;

import com.example.demo.model.dto.VehicleSearchRequest;
import com.example.demo.model.entity.Listing;
import com.example.demo.model.entity.ListingFitment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FitmentValidationService {

    public List<Listing> validateFitment(
            List<Listing> listings,
            VehicleSearchRequest request
    ) {

        return listings.stream()
                .filter(listing ->
                        isVehicleCompatible(listing, request)
                )
                .collect(Collectors.toList());
    }

    private boolean isVehicleCompatible(
            Listing listing,
            VehicleSearchRequest request
    ) {

        if (listing.getFitments() == null) return false;

        return listing.getFitments().stream()
                .anyMatch(fitment ->
                        request.getMake()
                                .equalsIgnoreCase(fitment.getMake())
                                &&
                                request.getModel()
                                        .equalsIgnoreCase(fitment.getModel())
                                &&
                                request.getYear() >= fitment.getYearStart()
                                &&
                                request.getYear() <= fitment.getYearEnd()
                );
    }
}