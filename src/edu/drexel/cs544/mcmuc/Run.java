package edu.drexel.cs544.mcmuc;

import edu.drexel.cs544.mcmuc.actions.Message;

public class Run {
    public static final int CONTROL_PORT = 31941;

    public static void main(String[] args) {
        Controller controller = Controller.getInstance();
        controller.useRoom("testchannel");
        controller.sendToRoom("testchannel", new Message("dustin@testchannel", "Hello World"));
    }
}