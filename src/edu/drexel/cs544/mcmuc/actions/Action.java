package edu.drexel.cs544.mcmuc.actions;

import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.Channel;

/**
 * Action is the base class for all message types. All messages must have an unique identifier,
 * which is automatically assigned for new Actions, and restored for Actions created through JSON
 * deserialization. If an Action is created by deserializing JSON, an expected action type that
 * is compared against the found type must be passed in.
 * 
 */
public abstract class Action extends ActionBase {

	public Action()
	{
		super();
	}
	
	public Action(JSONObject json, String expectedAction)
	{
		super(json,expectedAction);
	}
	
    public abstract void process(Channel channel);

    public abstract JSONObject toJSON();
}