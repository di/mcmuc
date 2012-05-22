package edu.drexel.cs544.mcmuc.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.JSON;

/**
 * RoomAction is the common parent class to the room management message types: 
 * ListRooms, UseRooms, Preserve, and Timeout. Each message type carries an array
 * of rooms (but this is optional for the ListRooms action). A RoomAction can be
 * created with or without an array of rooms - it is up to the child classes to
 * restrict how the message can be constructed if a list of rooms is required.
 *
 * @see ListRooms
 * @see UseRooms
 * @see Preserve
 * @see Timeout
 */
public class RoomAction extends Action implements JSON {

	private List<Integer> rooms;
	private String action;
	
	/**
	 * Any RoomAction can be represented at a minimum by a list of rooms and the underlying
	 * action to take, which is one of list-rooms, use-rooms, timeout, or preserve for the protocol.
	 * @param rooms the list of rooms
	 * @param action the underlying action
	 */
	public RoomAction(List<Integer> rooms,String action){
		this.rooms = rooms;
		this.action = action;
	}
	
	/**
	 * Allows a RoomAction to be created without a list of rooms - only used for list-rooms
	 * @param action the underlying action
	 * @see ListRooms
	 */
	public RoomAction(String action)
	{
		this.action = action;
	}
	
	/**
	 * Accessor for the list of rooms
	 * @return a List<Integer> of the rooms
	 */
	public List<Integer> getRooms()	{
		return this.rooms;
	}
	
	/**
	 * Deserializes a RoomAction from JSON - if a rooms key exists, the value is retrieved
	 * and stored in the RoomAction object.
	 * @param json
	 * @param action
	 */
	public RoomAction(JSONObject json, String action)
	{
		super(json,action);
		this.action = action;
		
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
	
	/**
	 * Serializes the RoomAction to JSON - if the object was created with a list of rooms,
	 * a rooms key/value pair is created in the JSON.
	 */
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
			json.put("action", action);
			json.put("uid", uid);

		} catch (JSONException e) {

		}
		
		return json;
	}
	
}