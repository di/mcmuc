package edu.drexel.cs544.mcmuc.actions;

import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.Channel;

/**
 * Action extends ActionBase and is the direct parent class for the various action types.
 * All child classes are concrete actions that represent a message and must provide their
 * processing and JSON serialization
 */
public abstract class Action extends ActionBase {

	/**
	 * Uses ActionBase to assign an UID
	 */
	public Action()
	{
		super();
	}
	
	/**
	 * Uses ActionBases to deserialize the JSON, checking the expection action type
	 * @param json
	 * @param expectedAction
	 */
	public Action(JSONObject json, String expectedAction)
	{
		super(json,expectedAction);
	}
	
	/**
	 * Process the action and send any necessary replies - implementation left to child classes
	 * @param channel
	 */
    public abstract void process(Channel channel);

    /**
     * Serializes the Action to JSON - implementation left to child classes
     * @return
     */
    public abstract JSONObject toJSON();
}