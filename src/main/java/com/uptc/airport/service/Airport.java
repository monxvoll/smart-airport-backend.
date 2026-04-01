package com.uptc.airport.service;

import com.uptc.airport.model.FlightEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class Airport {

    // Counting semaphore for limited resources (gates)
    private final Semaphore gates;

    // Array of locks for mutual exclusion (runways)
    private final ReentrantLock[] runways;

    // Tool to send messages via WebSocket
    private final SimpMessagingTemplate messagingTemplate;

    public Airport(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        // Simulating 3 available gates. 'true' ensures FIFO
        this.gates = new Semaphore(3, true);
        // Simulating 2 landing runways.
        this.runways = new ReentrantLock[2];
        for (int i = 0; i < 2; i++) {
            this.runways[i] = new ReentrantLock(true);
        }
    }

    // Helper method to broadcast WebSocket events and print to console
    private void sendLog(String airplane, String status, String details) {
        FlightEvent event = new FlightEvent(airplane, status, details);
        // Sends the JSON to the channel the frontend will subscribe to
        messagingTemplate.convertAndSend("/topic/airport", event);
        // Console output respecting your format
        System.out.println(airplane + " " + details);
    }

    // Compound synchronization: the thread needs both a gate and a runway.
    public void processFlight(String airplaneName) {
        try {
            sendLog(airplaneName, "APPROACHING", "approaching. Requesting a boarding gate...");

            // 1. Acquire a gate (Counting Semaphore). If none are available, the thread blocks here.
            gates.acquire();
            sendLog(airplaneName, "GATE_ASSIGNED", "has an assigned gate. Looking for a free runway...");

            // 2. Find a runway (Mutual Exclusion with Lock).
            int assignedRunway = -1;
            while (assignedRunway == -1) {
                for (int i = 0; i < runways.length; i++) {
                    if (runways[i].tryLock()) {
                        assignedRunway = i;
                        break;
                    }
                }
                if (assignedRunway == -1) {
                    Thread.sleep(100);
                }
            }

            // Using the runway
            sendLog(airplaneName, "LANDING", "landing on runway " + (assignedRunway + 1));
            // Thread.sleep(2000); // Simulating the time it takes to land.

            // Releasing the runway immediately after using it.
            runways[assignedRunway].unlock();
            sendLog(airplaneName, "TAXIING", "released runway " + (assignedRunway + 1) + " and is at the gate.");

            Thread.sleep(4000);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            sendLog(airplaneName, "EMERGENCY", "reported an emergency (Interrupted).");
        } finally {
            sendLog(airplaneName, "DEPARTED", "finished its service, released the gate, and took off.");
            gates.release();
        }
    }
}