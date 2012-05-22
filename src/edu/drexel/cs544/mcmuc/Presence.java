package edu.drexel.cs544.mcmuc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Presence extends Action implements JSON {
	
	private String from;
	private String status;
	private List<Certificate> keys = new ArrayList<Certificate>();
	
	
	public Presence(String from, String status, List<Certificate> keys){
		this.from = from;
		this.status= status;
		this.keys = keys;
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
		
	public Presence(JSONObject json)
	{
		super(json,"presence");
		
		try {
			JSONArray keys = json.getJSONArray("keys");
			
			if (keys != null) { 
				   int len = keys.length();
				   for (int i=0;i<len;i++){ 
					   this.keys.add((Certificate) keys.get(i));
				   } 
			} 
			
			this.from = json.getString("from");
			this.from = json.getString("status");			

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