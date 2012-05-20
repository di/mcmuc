package edu.drexel.cs544.mcmuc;

import org.json.JSONException;
import org.json.JSONObject;

public class PollPresence extends Action implements JSON {
	
	public PollPresence(JSONObject json)
	{
		super(json,"poll-presence");
	}
	
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