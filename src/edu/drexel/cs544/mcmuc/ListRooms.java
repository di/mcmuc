package edu.drexel.cs544.mcmuc;

import java.util.List;

import org.json.JSONObject;

public class ListRooms extends RoomAction {
	
	public ListRooms(List<Integer> rooms)
	{
		super(rooms,"list-rooms");
	}

	public ListRooms()
	{
		super("list-rooms");
	}

	public ListRooms(JSONObject json)
	{
		super(json,"list-rooms");		
	}
}
