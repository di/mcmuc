package edu.drexel.cs544.mcmuc;

import java.util.List;

public class PrimaryTimer implements Runnable {

	List<Integer> ports;

    public PrimaryTimer(List<Integer> ports) {
        this.ports = ports;
    }

    public void run() {
    	//TODO create a Timeout action for the rooms on ports and send it through Controller
    }
}