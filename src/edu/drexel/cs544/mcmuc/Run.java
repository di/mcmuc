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
            	Status s;
            	if(command.getArg(1).equalsIgnoreCase("Online"))
            		s = Status.Online;
            	else if(command.getArg(1).equalsIgnoreCase("Offline"))
            		s = Status.Offline;
            	else
            	{
            		System.err.println("Unknown status");
            		continue;
            	}
                controller.setRoomStatus(command.getArg(0), s); 
            } else if (command.getCommand() == CLICommand.Command.MESSAGE) {
            	controller.sendToRoom(command.getArg(0), new Message(controller.getUserName(command.getArg(0)), command.getArg(1)));
            } else if (command.getCommand() == CLICommand.Command.PVTMESSAGE) {
            	controller.sendToRoom(command.getArg(1), new Message(controller.getUserName(command.getArg(1)), command.getArg(2), command.getArg(0)));
            }
        }
    }
}