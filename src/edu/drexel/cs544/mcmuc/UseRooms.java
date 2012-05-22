package edu.drexel.cs544.mcmuc;

import java.util.List;

import org.json.JSONObject;

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
