package edu.drexel.cs544.mcmuc;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

abstract class Action {
	String uid;
	
	Action()
	{
		uid = UUID.randomUUID().toString();
	}
	
	Action(JSONObject json, String expectedAction)
	{
		try {
			if(!json.getString("action").equalsIgnoreCase(expectedAction))
				throw new JSONException(expectedAction + " action expected");
			
			uid = json.getString("uid");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}