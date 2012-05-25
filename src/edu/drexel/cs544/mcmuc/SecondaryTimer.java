package edu.drexel.cs544.mcmuc;

public class SecondaryTimer implements Runnable {

    int port;

    public SecondaryTimer(int port) {
        this.port = port;
    }

    public void run() {
    	//TODO Remove the room on port through Controller.leaveRoom()
    }
}