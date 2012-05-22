package edu.drexel.cs544.mcmuc;

import org.json.JSONObject;
/**
 * JSON is an interface implemented by each message type that provides a standard way 
 * to retrieve the JSON representation of a class.
 */
public interface JSON {
	/**
	 * Serializes the class in JSON notation
	 * @return the JSONObject that carries the required and optional fields that define 
	 * a message type
	 * @see JSONObject
	 */
	public JSONObject toJSON();
}