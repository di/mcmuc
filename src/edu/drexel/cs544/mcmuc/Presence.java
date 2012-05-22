package edu.drexel.cs544.mcmuc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Presence is used by each client to respond to a broadcast PollPresence.
 *
 * The JSON format of a Presence is: 
 * {'action':'presence','from':'<from>','status':'<status>'} if no public-keys are included or
 * {'action':'presence','from':'<from>','status':'<status>','keys':[<keys>]} if they are.
 */
public class Presence extends Action implements JSON {
	
	private String from;
	private String status;
	private List<Certificate> keys;
	
	
	public Presence(String from, String status, List<Certificate> keys) throws Exception{
		if(!status.equalsIgnoreCase("online") && !status.equalsIgnoreCase("offline"))
			throw new Exception("Status must be online or offline");
		
		this.from = from;
		this.status= status;
		this.keys = keys;
	}
	
	public Presence(String from, String status) throws Exception{
		if(!status.equalsIgnoreCase("online") && !status.equalsIgnoreCase("offline"))
			throw new Exception("Status must be online or offline");
		
		this.from = from;
		this.status= status;
	}
	
	public String getFrom(){
		return this.from;
	}
	
	public String getStatus(){
		return this.status;
	}
	
	public List<Certificate> getKeys(){
		return this.keys;
	}
		
	public Presence(JSONObject json) throws Exception
	{
		super(json,"presence");
		
		try {
			this.from = json.getString("from");			
			this.status = json.getString("status");	
			if(!this.status.equalsIgnoreCase("online") && !this.status.equalsIgnoreCase("offline"))
				throw new Exception("Status must be online or offline");
			
			if(json.has("keys"))
			{
				JSONArray keys = json.getJSONArray("keys");
				this.keys = new ArrayList<Certificate>(keys.length());
				
				if (keys != null) 
				{ 
					   int len = keys.length();
					   for (int i=0;i<len;i++)
					   { 
						   this.keys.add(new Certificate(keys.getJSONObject(i)));
					   } 
				} 
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}						
		
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		JSONArray list = new JSONArray();
		
		try {
			json.put("action", "presence");
			json.put("uid", uid);
			json.put("from", from);
			json.put("status", status);	
			if(keys != null)
			{			
				Iterator<Certificate> itr = keys.iterator();
				while(itr.hasNext())
				{
					list.put(itr.next().toJSON());
				}
				json.put("keys", list);
			}			
			
		} catch (JSONException e) {

		}
		
		return json;
	}
	
}