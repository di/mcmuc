package edu.drexel.cs544.mcmuc;

import java.util.List;

import org.json.JSONObject;

/**
 * The preserve action is used to reply to a timeout action, indicating that 
 * clients should continue to forward traffic for the included rooms.
 *
 * The JSON format of a Preserve is {'uid':'<uid>','action':'preserve','rooms':'<rooms>'}
 */
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
