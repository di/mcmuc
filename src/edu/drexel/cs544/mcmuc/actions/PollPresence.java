package edu.drexel.cs544.mcmuc.actions;

import org.json.JSONException;
import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.Channel;
import edu.drexel.cs544.mcmuc.DuplicateDetector;
import edu.drexel.cs544.mcmuc.JSON;
import edu.drexel.cs544.mcmuc.Room;

/**
 * PollPresence is used to query the other clients in a chat room about their online/offline status.
 * Clients in the room should respond with a broadcast Presence message.
 * 
 * The JSON format of a PollPresence is {'uid':'<uid>','action':'poll-presence'}
 */
public class PollPresence extends Action implements JSON {
	public static final String action = "poll-presence";
	
    /**
     * No options are required for the construction of a PollPresence message
     */
    public PollPresence() {
    }

    /**
     * Deserializes the JSON into a PollPresence object
     * is accepted.
     * 
     * @param json
     */
    public PollPresence(JSONObject json) {
        super(json, PollPresence.action);
    }

    /**
     * Serializes the PollPresence object into JSON
     */
    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {
            json.put("action", PollPresence.action);
            json.put("uid", uid);
        } catch (JSONException e) {

        }

        return json;
    }

    /**
     * Upon receiving a PollPresence, respond with a Presence action indicating the
     * user's current status for the room on the channel (online or offline). If the
     * message is not a duplicate, forward it on the channel.
     */
    @Override
    public void process(Channel channel) {
        class Runner implements Runnable {
            Channel channel;
            PollPresence message;

            Runner(PollPresence m, Channel c) {
                channel = c;
                message = m;
            }

            public void run() {
            	if(!DuplicateDetector.getInstance().isDuplicate(message.toJSON()))
            	{
	            	Room r = (Room)channel;
	            	Presence p = new Presence(r.getUserName(),r.getStatus());
	                channel.send(p);
	                channel.send(message);
                }
            }
        }
        Thread t = new Thread(new Runner(this,channel));
        t.start();

    }

}