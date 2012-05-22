package edu.drexel.cs544.mcmuc;

import edu.drexel.cs544.mcmuc.actions.Message;

public class Run {

    public static void main(String[] args) {
    	int[] portsInUse = {1,2,3};
        Room room = new Room("testchannel", portsInUse);
        Message message = new Message("dustin@testchannel", "Hello World");
        room.send(message);
    }
}