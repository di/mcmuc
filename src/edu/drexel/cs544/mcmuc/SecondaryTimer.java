package edu.drexel.cs544.mcmuc;

import java.util.List;

public class SecondaryTimer implements Runnable {

    List<Integer> ports;

    public SecondaryTimer(List<Integer> ports) {
        this.ports = ports;
    }

    public void run() {
    	//TODO Remove the rooms on ports in Controller's portsInUse
    }
}