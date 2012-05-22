package edu.drexel.cs544.mcmuc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListRooms extends Action implements JSON {

	private List<Integer> rooms;

	public ListRooms(List<Integer> rooms){
		this.rooms = rooms;
	}

	public ListRooms()
	{
	}

	public List<Integer> getRooms()	{
		return this.rooms;
	}

	public void addRoom(Integer room){
		this.rooms.add(room);
	}

	public ListRooms(JSONObject json)
	{
		super(json,"list-rooms");

		try {
			if(json.has("rooms"))
			{
				JSONArray rooms = json.getJSONArray("rooms");

				if (rooms != null)
				{ 
					int len = rooms.length();
					this.rooms = new ArrayList<Integer>(len);
					for (int i=0;i<len;i++)
					{ 
						this.rooms.add((Integer) rooms.get(i));
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
			if(rooms != null)
			{
				Iterator<Integer> itr = rooms.iterator();
				while(itr.hasNext())
				{
					list.put(itr.next());
				}
				json.put("rooms", list);
			}
			json.put("action", "list-rooms");
			json.put("uid", uid);

		} catch (JSONException e) {

		}

		return json;
	}

}