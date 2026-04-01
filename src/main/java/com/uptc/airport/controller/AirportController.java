package com.uptc.airport.controller;

import com.uptc.airport.model.Airplane;
import com.uptc.airport.service.Airport;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/airport")
@CrossOrigin(origins = "*") // Allows the frontend to connect from any port
public class AirportController {

    private final Airport airport;

    public AirportController(Airport airport) {
        this.airport = airport;
    }

    // Endpoint to start the simulation. Allows choosing the number of airplanes dynamically.
    @PostMapping("/start")
    public String startSimulation(@RequestParam(defaultValue = "10") int planes) {
        System.out.println("Starting simulation with " + planes + " airplanes from the REST endpoint.");

        for (int i = 1; i <= planes; i++) {
            Airplane airplane = new Airplane("Avianca-" + i, airport);
            airplane.start();

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return "Simulation started with " + planes + " airplanes";
    }
}