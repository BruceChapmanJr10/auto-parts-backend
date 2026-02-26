package com.example.demo.controller;

import com.example.demo.model.dto.VinDecodeResponse;
import com.example.demo.service.vin.VinService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vin")
@CrossOrigin
public class VinController {

    private final VinService vinService;

    public VinController(VinService vinService) {
        this.vinService = vinService;
    }

    @GetMapping("/{vin}")
    public VinDecodeResponse decode(
            @PathVariable String vin
    ) {
        return vinService.decode(vin);
    }
}