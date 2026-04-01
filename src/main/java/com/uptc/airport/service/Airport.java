package com.uptc.airport.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class Airport {

    // Counting semaphore for limited resources (gates)
    private final Semaphore gates;

    // Array of locks for mutual exclusion (runways)
    private final ReentrantLock[] runways;

    public Airport() {
        // Simulating 3 available gates. 'true' ensures FIFO
        this.gates = new Semaphore(3, true);
        // Simulating 2 landing runways.
        this.runways = new ReentrantLock[2];
        for (int i = 0; i < 2; i++) {
            this.runways[i] = new ReentrantLock(true);
        }
    }

    // Compound synchronization: the thread needs both a gate and a runway.
    public void processFlight(String airplaneName) {
        try {
            System.out.println( airplaneName + " approaching. Requesting a boarding gate...");

            // 1. Acquire a gate (Counting Semaphore). If none are available, the thread blocks here.
            gates.acquire();
            System.out.println( airplaneName + " has an assigned gate. Looking for a free runway...");

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

            //  Using the runway
            System.out.println( airplaneName + " landing on runway " + (assignedRunway + 1));
            // Thread.sleep(2000); // Simulating the time it takes to land.

            // Releasing the runway immediately after using it.
            runways[assignedRunway].unlock();
            System.out.println( airplaneName + " released runway " + (assignedRunway + 1) + " and is at the gate.");


            Thread.sleep(4000);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println( airplaneName + " reported an emergency (Interrupted).");
        } finally {

            System.out.println( airplaneName + " finished its service, released the gate, and took off.");
            gates.release();
        }
    }
}