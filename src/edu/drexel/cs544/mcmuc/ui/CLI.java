package edu.drexel.cs544.mcmuc.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.drexel.cs544.mcmuc.ui.CLICommand.Command;

/**
 * A very basic Command-line interface - possible commands are:
 * message @<room-name> <message>
 * message <user-name>@<room-name> <message>
 * message key='<public-key-file>' <user-name>@<room-name> <message>
 * presence @<room-name> <status>
 * use-room <user-name>@<room-name>
 * leave-room @<room-name>
 * add-key @<room-name> public='<public-key-file>' private='<public-key-file>'
 * remove-key @<room-name> public='<public-key-file>'
 * exit
 */
public class CLI extends Thread implements UI {

    AtomicBoolean command_is_ready = new AtomicBoolean();
    CLICommand command = null;
    private String useRoomRegex = "(?i)use-room (\\w+)@(\\w+)";
    private String leaveRoomRegex = "(?i)leave-room @(\\w+)";
    private String presenceRegex = "(?i)presence @(\\w+) (\\w+)";
    private String messageRegex = "(?i)message @(\\w+) (.+)";
    private String privateMessageRegex = "(?i)message (\\w+)@(\\w+) (.+)";
    private String addKeyRegex = "(?i)add-key @(\\w+) public='(.+)' private='(.+)'";
    private String removeKeyRegex = "(?i)remove-key @(\\w+) public='(.+)'";
    private String secureMessageRegex = "(?i)message key='(.+)' (\\w+)@(\\w+) (.+)";

    /**
     * Initializes the CLI, setting the command ready flag to false
     */
    public CLI() {
        command_is_ready.set(false);
    }

    /**
     * Matches a given input string against a regular expression representing each possible command type.
     * If a match is found the string is separated into a command and its arguments. If no match is found,
     * an error and the set of available commands is displayed.
     * 
     * @param s String to parse into a command and its arguments
     */
    public synchronized void input(String s) {

        if (s == null) {
            // do nothing
        } else if (s.equalsIgnoreCase("exit"))
            sendCommand(new CLICommand(CLICommand.Command.EXIT, null));
        else if (s.matches(useRoomRegex))
            sendCommand(new CLICommand(CLICommand.Command.USEROOM, getCmdArgs(useRoomRegex, s, 2)));
        else if (s.matches(leaveRoomRegex))
            sendCommand(new CLICommand(CLICommand.Command.LEAVEROOM, getCmdArgs(leaveRoomRegex, s, 1)));
        else if (s.matches(presenceRegex))
            sendCommand(new CLICommand(CLICommand.Command.PRESENCE, getCmdArgs(presenceRegex, s, 2)));
        else if (s.matches(messageRegex))
            sendCommand(new CLICommand(CLICommand.Command.MESSAGE, getCmdArgs(messageRegex, s, 2)));
        else if (s.matches(privateMessageRegex))
            sendCommand(new CLICommand(CLICommand.Command.PVTMESSAGE, getCmdArgs(privateMessageRegex, s, 3)));
        else if (s.matches(addKeyRegex))
            sendCommand(new CLICommand(CLICommand.Command.ADDKEY, getCmdArgs(addKeyRegex, s, 3)));
        else if (s.matches(removeKeyRegex))
            sendCommand(new CLICommand(CLICommand.Command.REMOVEKEY, getCmdArgs(removeKeyRegex, s, 2)));
        else if (s.matches(secureMessageRegex))
            sendCommand(new CLICommand(CLICommand.Command.SECUREMESSAGE, getCmdArgs(secureMessageRegex, s, 4)));
        else {
            alert("Received an unknown command: \"" + s + "\"");
            String cmds = "Available commands:\n" + "\t message @<room-name> <message>\n" + "\t message <user-name>@<room-name> <message>\n" + "\t presence @<room-name> <status>\n" + "\t use-room <user-name>@<room-name>\n" + "\t leave-room @<room-name>\n" + "\t add-key @<room-name> public='<key-file>' private='<key-file>'\n" + "\t remove-key @<room-name> public='<key-file>'\n" + "\t message key='<public-key-file>' <user-name>@<room-name> <message>\n" + "\t exit\n";
            alert(cmds);
        }
    }

    /**
     * Given an input string and regular expression, return the first n matching groups as an array
     * 
     * @param regex String regular expression to match input against
     * @param input String input
     * @param n int first n matching groups to return
     * @return String[] first n matching groups
     */
    private String[] getCmdArgs(String regex, String input, int n) {
        String args[] = new String[n];

        Matcher matcher = Pattern.compile(regex).matcher(input);
        matcher.find();

        for (int i = 0; i < n; i++)
            args[i] = matcher.group(i + 1);

        return args;
    }

    /**
     * Output a string to the UI
     * 
     * @param s String to output
     */
    public void output(String s) {
        System.out.print('\r' + s + "\n> ");
    }

    /**
     * Output a string to the UI, as an alert
     * 
     * @param s String to output
     */
    public void alert(String s) {
        output("* " + s);
    }

    /**
     * Reads input from System.in and sends it to input() for parsing. Repeat until the command is 'exit'.
     */
    public void run() {
        this.alert("McMUC CLI waiting for a command. (Type 'exit' or CTRL-C to quit)");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String s = null;
            try {
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
     * Sets the thread's command and sets the commandy_is_ready flag to true. Resumes the thread from a
     * previous call to wait().
     * 
     * @param c CLICommand to set
     */
    protected synchronized void sendCommand(CLICommand c) {
        this.command = c;
        command_is_ready.set(true);
        notifyAll();
    }

    /**
     * Waits the thread until the command_is_ready flag is set, then resets the flag and returns the command.
     * 
     * @return CLICommand command that was set.
     */
    public synchronized CLICommand getNextCommand() {
        try {
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