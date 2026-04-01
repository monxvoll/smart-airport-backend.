package com.uptc.airport;

import com.uptc.airport.model.Airplane;
import com.uptc.airport.service.Airport;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SimulationRunner implements CommandLineRunner {

    private final Airport airport;

    public SimulationRunner(Airport airport) {
        this.airport = airport;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting..");

        // Creating 10 airplanes (threads) that arrive almost at the same time
        for (int i = 1; i <= 10; i++) {
            Airplane airplane = new Airplane("Avianca-" + i, airport);
            airplane.start();
            Thread.sleep(300); // Delay between arrivals
        }
    }
}