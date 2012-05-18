package edu.drexel.cs544.mcmuc;

public class Run {

    public static void main(String[] args) {
        Room room = new Room("testchannel");
        room.send("Hello World");
    }
}