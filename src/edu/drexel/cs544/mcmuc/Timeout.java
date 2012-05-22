package edu.drexel.cs544.mcmuc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Timeout extends Action implements JSON {
	
	public List<Integer> rooms = new ArrayList<Integer>();
	
	public Timeout(List<Integer> rooms){
		this.rooms = rooms;
	}
		
	public List<Integer> getRooms()	{
		return this.rooms;
	}
	
	public void addRoom(Integer room){
		this.rooms.add(room);
	}	
	public Timeout(JSONObject json)
	{
		super(json,"timeout");
		
		try {
			JSONArray rooms = json.getJSONArray("rooms");
			
			if (rooms != null) { 
				   int len = rooms.length();
				   for (int i=0;i<len;i++){ 
					   this.rooms.add((Integer) rooms.get(i));
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
			if(rooms != null)
			{			
				Iterator<Integer> itr = rooms.iterator();
				while(itr.hasNext())
				{
					list.put(itr.next());
				}
				json.put("rooms", list);
			}
			json.put("action", "timeout");
			json.put("uid", uid);
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return json;
	}
	
}