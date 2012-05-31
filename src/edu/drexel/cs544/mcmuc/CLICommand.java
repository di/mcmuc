package edu.drexel.cs544.mcmuc;

public class CLICommand {

    private CLICommand.Command c;
    private String[] args;

    public enum Command {
        MESSAGE, PRESENCE, USEROOM, EXIT;
    }

    public CLICommand(CLICommand.Command c, String[] args) {
        this.c = c;
        this.args = args;
    }

    public CLICommand.Command getCommand() {
        return this.c;
    }

    public String[] getArgs() {
        return args;
    }

    public String getArg(int i) {
        return args[i];
    }
}
