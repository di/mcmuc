package edu.drexel.cs544.mcmuc.actions;

import java.util.List;

import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.Channel;

/**
 * The timeout action is used by clients to find channels clients are no longer
 * actively using to chat, and thus whose allocated resources can be freed.
 * The timeout is used to ask the other clients which of the provided rooms they
 * are using.
 * 
 * The JSON format of a Timeout is {'uid':'<uid>','action':'timeout','rooms':'<rooms>'}
 * 
 */
public class Timeout extends RoomAction {
	public static final String action = "timeout";
	
    public Timeout(List<Integer> rooms) {
        super(rooms, Timeout.action);
    }

    public Timeout(JSONObject json) {
        super(json, Timeout.action);
    }

    @Override
    public void process(Channel channel) {
        // TODO Auto-generated method stub
    }
}
