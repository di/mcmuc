package edu.drexel.cs544.mcmuc;

import org.json.JSONObject;

public class Controller extends Channel {

    public static final int CONTROL_PORT = 31941;

    Controller(int port) {
        super(port);
    }

    private static final Controller instance = new Controller(CONTROL_PORT);

    public static Controller getInstance() {
        return instance;
    }

    @Override
    public void handleNewMessage(JSONObject jo) {
        // TODO Auto-generated method stub

    }

    public void display(String displayString) {
        System.out.println(displayString);
    }
}