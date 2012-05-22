package edu.drexel.cs544.mcmuc;

import java.util.List;

import org.json.JSONObject;

public class Preserve extends RoomAction {

	public Preserve(List<Integer> rooms)
	{
		super(rooms,"preserve");
	}
	
	public Preserve(JSONObject json)
	{
		super(json,"preserve");		
	}

	
}
