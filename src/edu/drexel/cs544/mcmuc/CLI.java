package edu.drexel.cs544.mcmuc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.drexel.cs544.mcmuc.CLICommand.Command;

/**
 * A very basic Command-Line Interface
 */
public class CLI extends Thread implements UI {

    AtomicBoolean command_is_ready = new AtomicBoolean();
    CLICommand command = null;
    private String useRoomRegex = "(?i)use-room (\\w+)@(\\w+)";
    private String presenceRegex = "(?i)presence @(\\w+) (\\w+)";
    private String messageRegex = "(?i)message @(\\w+) (.+)";
    private String privateMessageRegex = "(?i)message (\\w+)@(\\w+) (.+)";

    public CLI() {
        command_is_ready.set(false);
    }

    /**
     * sends the Controller command to exit
     * 
     * @param s
     */
    public synchronized void input(String s) {
        Matcher matcher = null;

        if (s == null) {
            // do nothing
        } else if (s.equalsIgnoreCase("exit")) {
            sendCommand(new CLICommand(CLICommand.Command.EXIT, null));
        } else if (s.matches(useRoomRegex)) {
            matcher = Pattern.compile(useRoomRegex).matcher(s);
            matcher.find();
            String[] args = { matcher.group(1), matcher.group(2) };
            sendCommand(new CLICommand(CLICommand.Command.USEROOM, args));
        } else if (s.matches(presenceRegex)) {
            matcher = Pattern.compile(presenceRegex).matcher(s);
            matcher.find();
            String[] args = { matcher.group(1), matcher.group(2) };
            sendCommand(new CLICommand(CLICommand.Command.PRESENCE, args));
        } else if (s.matches(messageRegex)) {
            matcher = Pattern.compile(messageRegex).matcher(s);
            matcher.find();
            String[] args = { matcher.group(1), matcher.group(2) };
            sendCommand(new CLICommand(CLICommand.Command.MESSAGE, args));
        } else if (s.matches(privateMessageRegex)) {
            matcher = Pattern.compile(privateMessageRegex).matcher(s);
            matcher.find();
            String[] args = { matcher.group(1), matcher.group(2), matcher.group(3) };
            sendCommand(new CLICommand(CLICommand.Command.PVTMESSAGE, args));
        } else {
            System.err.println("Received an unknown command: " + s);	
            String rval = "Available commands: [";
            String delim = "";
            for (CLICommand.Command cmd : CLICommand.Command.values()) {
                rval += delim + cmd;
                delim = ", ";
            }
            rval += "]\n";
            System.out.println(rval);
        }
    }

    /**
     * Output a string to the UI
     * 
     * @param s
     */
    public void output(String s) {
        System.out.print('\r' + s + "\n> ");
    }

    public void run() {
        System.out.println("(Type 'exit' to quit.)");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String s = null;
            try {
                System.out.print(">");
                s = reader.readLine();
                if (s == null) {
                    System.err.println("STDIN is closed or set to null. Use Ctrl-C to close.");
                    return;
                }
            } catch (IOException e) {
                System.err.println("IOException while waiting for command input");
                return;
            }
            input(s);
            if (command_is_ready.get() && (command.equals(Command.EXIT))) {
                return;
            }
        }
    }

    /**
     * Send a command.
     * 
     * @param c
     */
    protected synchronized void sendCommand(CLICommand c) {
        this.command = c;
        command_is_ready.set(true);
        notifyAll();
    }

    public synchronized CLICommand getNextCommand() {
        try {
            this.output("McMUC TUI waiting for a command");
            while (!command_is_ready.get()) {
                wait(); // wait for a command
            }
            command_is_ready.set(false);
        } catch (InterruptedException e) {
            System.err.println("Interrupted while waiting for a command!");
        }
        return this.command;
    }
}