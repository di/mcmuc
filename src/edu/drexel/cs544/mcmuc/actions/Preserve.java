package edu.drexel.cs544.mcmuc.actions;

import java.util.List;

import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.Channel;

/**
 * The preserve action is used to reply to a timeout action, indicating that
 * clients should continue to forward traffic for the included rooms.
 * 
 * The JSON format of a Preserve is {'uid':'<uid>','action':'preserve','rooms':'<rooms>'}
 */
public class Preserve extends RoomAction {
	public static final String action = "preserve";
	
    public Preserve(List<Integer> rooms) {
        super(rooms, Preserve.action);
    }

    public Preserve(JSONObject json) {
        super(json, Preserve.action);
    }

    @Override
    public void process(Channel channel) {
        // TODO Auto-generated method stub
    }
}