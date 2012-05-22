package edu.drexel.cs544.mcmuc.actions;

import java.util.List;

import org.json.JSONObject;

/**
 * The list-rooms action is used by clients to update channel use information. 
 * If the optional collection of rooms is not included, this signals that the 
 * client wants to know about all channels in use. If a collection of rooms is 
 * provided, the client wishes to know the status of just those channels. 
 * 
 * The JSON format of a ListRooms is either
 * {'uid':'<uid>','action':'list-rooms','rooms':'<rooms>'} or
 * {'uid':'<uid>','action':'list-rooms'}
 */
public class ListRooms extends RoomAction {
	
	public ListRooms(List<Integer> rooms)
	{
		super(rooms,"list-rooms");
	}

	/**
	 * Allows ListRooms to be created without a list of rooms - the only such
	 * RoomAction child allowed to.
	 */
	public ListRooms()
	{
		super("list-rooms");
	}

	public ListRooms(JSONObject json)
	{
		super(json,"list-rooms");		
	}
}
