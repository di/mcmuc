package edu.drexel.cs544.mcmuc.ui;

/**
 * Represents a command and the associated arguments issued by the user at the interactive command-line
 * interface. 
 */
public class CLICommand {

    private CLICommand.Command c;
    private String[] args;

    public enum Command {
        MESSAGE, PVTMESSAGE, PRESENCE, USEROOM, LEAVEROOM, ADDKEY, REMOVEKEY, SECUREMESSAGE, EXIT;
    }

    /**
     * Initializes the command and its associated arguments
     * @param c Command
     * @param args String[] associated arguments
     */
    public CLICommand(CLICommand.Command c, String[] args) {
        this.c = c;
        this.args = args;
    }

    /**
     * Gets the command
     * @return Command the command
     */
    public CLICommand.Command getCommand() {
        return this.c;
    }

    /**
     * Gets the commands associated arguments
     * @return String[] the arguments
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * Gets the command argument at the i-th position
     * @param i int position of argument to retrieve
     * @return String argument at position i
     */
    public String getArg(int i) {
        return args[i];
    }
}
