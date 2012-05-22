package edu.drexel.cs544.mcmuc;

import java.util.List;

import org.json.JSONObject;

public class Timeout extends RoomAction {
	
	public Timeout(List<Integer> rooms)
	{
		super(rooms,"timeout");
	}
	
	public Timeout(JSONObject json)
	{
		super(json,"timeout");		
	}

	
}
