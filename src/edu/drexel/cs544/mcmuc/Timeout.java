package edu.drexel.cs544.mcmuc;

import org.json.JSONException;
import org.json.JSONObject;

public class Timeout extends Action implements JSON {
	
	public Timeout(JSONObject json)
	{
		super(json,"timeout");
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		
		try {
			json.put("action", "timeout");
			json.put("uid", uid);
		} catch (JSONException e) {

		}
		
		return json;
	}
	
}