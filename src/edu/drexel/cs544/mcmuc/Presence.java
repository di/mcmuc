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
	
	/**
	 * Initializes the from, status, and keys attributes of the Presence class. Status must be either:
	 * 'online' or 'offline'
	 * @param from
	 * @param status must be 'online' or 'offline'
	 * @param keys
	 * @throws Exception thrown if status is not 'online' or 'offline'
	 */
	private void init(String from, String status, List<Certificate> keys) throws Exception
	{
		if(!status.equalsIgnoreCase("online") && !status.equalsIgnoreCase("offline"))
			throw new Exception("Status must be online or offline");
		
		this.from = from;
		this.status= status;
		this.keys = keys;
	}
	
	/**
	 * Calls init with passed-in parameters - exception is forwarded from init. Use if user desires
	 * to include public-keys with presence.
	 * @param from
	 * @param status
	 * @param keys
	 * @throws Exception
	 */
	public Presence(String from, String status, List<Certificate> keys) throws Exception{
		init(from,status,keys);
	}
	
	/**
	 * Calls init with passed-in parameters - exception is forwarded from init. Use if no keys are
	 * required to be sent along with presence.
	 * @param from
	 * @param status
	 * @throws Exception
	 */
	public Presence(String from, String status) throws Exception{
		init(from,status,null);
	}
	
	/**
	 * Accessor for message's from field
	 * @return
	 */
	public String getFrom(){
		return this.from;
	}
	
	/**
	 * Accessor for message's status field
	 * @return
	 */
	public String getStatus(){
		return this.status;
	}
	
	/**
	 * Accessor for message's (optional) list of public-key certificates
	 * @return
	 */
	public List<Certificate> getKeys(){
		return this.keys;
	}
	
	/**
	 * Deserializes JSON into a Presence object. The status key must have 'offline' or 'online' as its value.
	 * The keys key/value pair is optional.
	 * @param json
	 * @throws Exception thrown if status is not 'online' or 'offline'
	 */
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
	
	/**
	 * Serializes Presence object into JSON. keys key/value pair is only set if object was created with
	 * a list of keys.
	 */
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