package edu.drexel.cs544.mcmuc;

import edu.drexel.cs544.mcmuc.actions.Message;
import edu.drexel.cs544.mcmuc.actions.Presence.Status;

public class Run {
    public static final int CONTROL_PORT = 31941;

    public static void main(String[] args) {

        CLI cli = new CLI();
        cli.start();

        Controller controller = Controller.getInstance();
        controller.setUI(cli);

        controller.useRoom("testchannel", "dustin@testchannel");
        // controller.useRoom(52316);
        controller.sendToRoom("testchannel", new Message("dustin@testchannel", "Hello World"));
        controller.setRoomStatus("testchannel", Status.Offline);

        while (true) {
            CLI.Command command = getNextCommand(cli);
            // Stop and kill this program
            if (command == CLI.Command.EXIT) {
                System.err.println("Exit command received, shutting down...");
                System.exit(0);
                // } else if (command == TUI.Command.SOMETHING) {
                // Do something
            }
        }
    }

    private static CLI.Command getNextCommand(CLI cli) {
        // wait for a command
        try {
            cli.output("McMUC TUI waiting for a command");
            cli.await();
        } catch (InterruptedException e) {
            System.err.println("Interrupted while waiting for a command!");
        }
        return cli.getCommand();
    }
}