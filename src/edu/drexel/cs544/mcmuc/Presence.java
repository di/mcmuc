package edu.drexel.cs544.mcmuc;

import org.json.JSONException;
import org.json.JSONObject;

public class Presence extends Action implements JSON {
	
	public Presence(JSONObject json)
	{
		super(json,"presence");
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		
		try {
			json.put("action", "presence");
			json.put("uid", uid);
		} catch (JSONException e) {

		}
		
		return json;
	}
	
}