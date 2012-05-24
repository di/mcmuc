package edu.drexel.cs544.mcmuc;

import edu.drexel.cs544.mcmuc.actions.Message;
import edu.drexel.cs544.mcmuc.actions.Presence.Status;

public class Run {
    public static final int CONTROL_PORT = 31941;

    public static void main(String[] args) {
        Controller controller = Controller.getInstance();
        System.out.println(controller.portsInUse);
        controller.useRoom("testchannel","dustin@testchannel");
        // controller.useRoom(52316);
        System.out.println(controller.portsInUse);
        controller.sendToRoom("testchannel", new Message("dustin@testchannel", "Hello World"));
        controller.setRoomStatus("testchannel", Status.Offline);
    }
}