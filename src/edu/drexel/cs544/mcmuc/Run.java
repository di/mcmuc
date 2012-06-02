package edu.drexel.cs544.mcmuc;

import edu.drexel.cs544.mcmuc.actions.Action;
import edu.drexel.cs544.mcmuc.actions.Message;
import edu.drexel.cs544.mcmuc.actions.Presence.Status;

public class Run {
    public static final int CONTROL_PORT = 31941;

    public static void main(String[] args) {

        CLI cli = new CLI();
        cli.start();

        Controller controller = Controller.getInstance();
        controller.setUI(cli);

        controller.useRoom("testchannel", "dustin");
        // controller.useRoom(52316);
        controller.sendToRoom("testchannel", new Message("dustin", "Hello World"));
        controller.setRoomStatus("testchannel", Status.Offline);

        while (true) {
            CLICommand command = cli.getNextCommand();
            if (command.getCommand() == CLICommand.Command.EXIT) { // Stop and kill this program
                System.err.println("Exit command received, shutting down...");
                System.exit(0);
            } else if (command.getCommand() == CLICommand.Command.USEROOM) {
                controller.useRoom(command.getArg(1), command.getArg(0));
            } else if (command.getCommand() == CLICommand.Command.PRESENCE) {
                controller.setRoomStatus(command.getArg(0), Status.valueOf(command.getArg(1))); 
            } else if (command.getCommand() == CLICommand.Command.MESSAGE) {
            	Integer roomPort = controller.roomNames.get(command.getArg(0));
            	Room room = (Room)controller.channels.get(roomPort);
            	controller.sendToRoom(command.getArg(0), new Message(room.getUserName(), command.getArg(1)));
            } else if (command.getCommand() == CLICommand.Command.PVTMESSAGE) {
            	Integer roomPort = controller.roomNames.get(command.getArg(1));
            	Room room = (Room)controller.channels.get(roomPort);
            	controller.sendToRoom(command.getArg(1), new Message(room.getUserName(), command.getArg(2), command.getArg(0)));
            }
        }
    }
}