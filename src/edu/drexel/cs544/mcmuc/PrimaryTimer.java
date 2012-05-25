package edu.drexel.cs544.mcmuc;

public class PrimaryTimer implements Runnable {

	int port;

    public PrimaryTimer(int port) {
        this.port = port;
    }
    
    public PrimaryTimer(Integer port)
    {
    	this.port = port;
    }

    public void run() {
    	//TODO create a Timeout action for the room on port and send it through Controller
    }
}