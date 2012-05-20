package edu.drexel.cs544.mcmuc;

import org.json.JSONException;
import org.json.JSONObject;

public class ListRooms extends Action implements JSON {
	
	public ListRooms(JSONObject json)
	{
		super(json,"list-rooms");
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		
		try {
			json.put("action", "list-rooms");
			json.put("uid", uid);
		} catch (JSONException e) {

		}
		
		return json;
	}
	
}