package edu.drexel.cs544.mcmuc.actions;

import java.util.List;

import org.json.JSONObject;

/**
 * The use-rooms action is used to reply to a list-rooms action. Clients will either 
 * reply with a collection of all channels they know are in use or the subset of 
 * channels in use that were asked about in the original list-rooms action. In 
 * addition, use-rooms is also used to create a new room.
 *
 * The JSON format of a UseRooms is {'uid':'<uid>','action':'use-rooms','rooms':'<rooms>'}
 */
public class UseRooms extends RoomAction {
	
	public UseRooms(List<Integer> rooms)
	{
		super(rooms,"use-rooms");
	}
	
	public UseRooms(JSONObject json)
	{
		super(json,"use-rooms");		
	}

	
}
