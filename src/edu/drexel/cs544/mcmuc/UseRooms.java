package edu.drexel.cs544.mcmuc;

import org.json.JSONException;
import org.json.JSONObject;

public class UseRooms extends Action implements JSON {
	
	public UseRooms(JSONObject json)
	{
		super(json,"use-rooms");
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		
		try {
			json.put("action", "use-rooms");
			json.put("uid", uid);
		} catch (JSONException e) {

		}
		
		return json;
	}
	
}