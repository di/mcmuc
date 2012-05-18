package edu.drexel.cs544.mcmuc;

public class Run {

    public static void main(String[] args) {
    	int[] portsInUse = {1,2,3};
        Room room = new Room("testchannel", portsInUse);
        room.send("Hello World");
    }
}