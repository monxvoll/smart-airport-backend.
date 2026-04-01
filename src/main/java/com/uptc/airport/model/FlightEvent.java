package com.uptc.airport.model;

// Spring Boot will transform this into a JSON for the websocket
public record FlightEvent(String airplane, String status, String details) {
}