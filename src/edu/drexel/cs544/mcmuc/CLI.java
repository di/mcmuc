package edu.drexel.cs544.mcmuc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A very basic Command-Line Interface
 */
public class CLI extends Thread implements UI {
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
            sendRawCommand(s);
            if (command_is_ready.get() && (command.equals(Command.EXIT))) {
                System.err.println("Exiting...");
                return;
            }
        }
    }

    AtomicBoolean command_is_ready = new AtomicBoolean();
    Command command = null;

    public enum Command {
        EXIT;
    }

    public CLI() {
        command_is_ready.set(false);
    }

    /**
     * @return the last command received.
     */
    public Command getCommand() {
        return command;
    }

    /**
     * sends the Controller command to exit
     * 
     * @param s
     */
    public synchronized void sendRawCommand(String s) {
        if (s == null) {
            // do nothing
        } else if (s.equalsIgnoreCase("exit")) {
            sendCommand(Command.EXIT);
        } else {
            System.err.println("Received an unknown command: " + s);
            String rval = "Available commands: [";
            String delim = "";
            for (Command cmd : Command.values()) {
                rval += delim + cmd;
                delim = ", ";
            }
            rval += "]\n";
            System.out.println(rval);
        }
    }

    /**
     * Send a command.
     */
    protected synchronized void sendCommand(Command c) {
        command = c;
        command_is_ready.set(true);
        notifyAll();
    }

    /**
     * This function blocks until a command is received.
     */
    public synchronized void await() throws InterruptedException {
        while (!command_is_ready.get()) {
            wait();
        }
        command_is_ready.set(false);
    }
}