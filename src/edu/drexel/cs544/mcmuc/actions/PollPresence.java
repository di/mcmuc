package edu.drexel.cs544.mcmuc.actions;

import org.json.JSONException;
import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.JSON;

/**
 * PollPresence is used to query the other clients in a chat room about their online/offline status.
 * Clients in the room should respond with a broadcast Presence message.
 * 
 * The JSON format of a PollPresence is {'uid':'<uid>','action':'poll-presence'}
 */
public class PollPresence extends Action implements JSON {
	
	/**
	 * No options are required for the construction of a PollPresence message
	 */
	public PollPresence()
	{
	}
	
	/**
	 * Deserializes the JSON into a PollPresence object
	 * is accepted.
	 * @param json
	 */
	public PollPresence(JSONObject json)
	{
		super(json,"poll-presence");
	}
	
	/**
	 * Serializes the PollPresence object into JSON
	 */
	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		
		try {
			json.put("action", "poll-presence");
			json.put("uid", uid);
		} catch (JSONException e) {

		}
		
		return json;
	}
	
}