package com.uptc.airport.model;

import com.uptc.airport.service.Airport;

public class Airplane extends Thread {

    private final Airport airport;

    public Airplane(String name, Airport airport) {
        super(name);
        this.airport = airport;
    }

    @Override
    public void run() {
        airport.processFlight(this.getName());
    }
}