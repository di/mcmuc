package edu.drexel.cs544.mcmuc;

import org.json.JSONException;
import org.json.JSONObject;

public class Preserve extends Action implements JSON {
	
	public Preserve(JSONObject json)
	{
		super(json,"preserve");
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		
		try {
			json.put("action", "preserve");
			json.put("uid", uid);
		} catch (JSONException e) {

		}
		
		return json;
	}
	
}