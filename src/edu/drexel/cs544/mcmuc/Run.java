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
            CLICommand command = cli.getNextCommand();
            if (command.getCommand() == CLICommand.Command.EXIT) { // Stop and kill this program
                System.err.println("Exit command received, shutting down...");
                System.exit(0);
                // } else if (command == TUI.Command.SOMETHING) {
                // Do something
            } else if (command.getCommand() == CLICommand.Command.USEROOM) {
                controller.useRoom(command.getArg(1), command.getArg(0));
                controller.setRoomStatus(command.getArg(1), Status.Online);
            }
        }
    }
}