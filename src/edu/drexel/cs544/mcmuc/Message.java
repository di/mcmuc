package edu.drexel.cs544.mcmuc;

import org.json.JSONException;
import org.json.JSONObject;

public class Message implements Action {

	private String from;
	private String body;
	private String to;
	private Boolean hasTo;
	private Certificate key;
	private Boolean hasKey;
	
	private void init(String from, String body, String to, Certificate key)
	{
		this.from = from;
		this.body = body;
		if(to != null)
		{
			this.to = to;
			this.hasTo = true;
		}
		else
			this.hasTo = false;
		
		if(key != null)
		{
			this.key = key;
			this.hasKey = true;
		}
		else
			this.hasKey = false;
	}
	public Message(String from, String body)
	{
		init(from,body,null,null);
	}
	
	public Message(String from, String body, String to)
	{
		init(from,body,to,null);
	}
	
	public Message(String from, String body, Certificate key)
	{
		init(from,body,null,key);
	}
	
	public Message(String from, String body, String to, Certificate key)
	{
		init(from,body,to,key);
	}
	
	public Message(JSONObject json)
	{
			try {
				if(!json.getString("action").equalsIgnoreCase("message"))
					throw new JSONException("Incorrect action");
				
				String from = json.getString("from");
				String body = json.getString("body");
				
				String to = null;
				Certificate key = null;
				
				if(json.has("to"))
					to = json.getString("to");
				
				if(json.has("key"))
					key = new Certificate(json.getJSONObject("key"));
				
				init(from,body,to,key);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
	}

	public String getFrom() {
		return from;
	}

	public String getBody() {
		return body;
	}

	public String getTo() {
		return to;
	}

	public Boolean hasTo() {
		return hasTo;
	}

	public Certificate getKey() {
		return key;
	}

	public Boolean hasKey() {
		return hasKey;
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		
		try {
			json.put("action", "message");
			json.put("from", from);
			json.put("body", body);
			if(hasTo)
				json.put("to", to);
			if(hasKey)
				json.put("key", key.toJSON());
		} catch (JSONException e) {

		}
		
		return json;
	}
	
}