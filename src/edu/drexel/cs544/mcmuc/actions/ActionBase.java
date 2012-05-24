package edu.drexel.cs544.mcmuc.actions;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * ActionBase is the base class for all message types. All messages must have an unique identifier,
 * which is automatically assigned for new Actions, and restored for Actions created through JSON
 * deserialization. If an Action is created by deserializing JSON, an expected action type that
 * is compared against the found type must be passed in.
 * 
 */
public class ActionBase {
    String uid;

    /**
     * The default constructor for ActionBase only assigns a random UUID as the message's unique
     * identifier
     */
    public ActionBase() {
        uid = UUID.randomUUID().toString();
    }

    /**
     * The constructor checks the 'action' key/value against the user expected type and
     * sets the unique identifier to whatever is stored in the JSON. It does not perform any
     * additional deserialization - this is left to the child classes to implement.
     * 
     * @param json the serialized JSON representation of the Action
     * @param expectedAction the expected value of the 'action' key in the JSON
     */
    public ActionBase(JSONObject json, String expectedAction) {
        try {
            if (!json.getString("action").equalsIgnoreCase(expectedAction))
                throw new JSONException(expectedAction + " action expected");

            uid = json.getString("uid");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Does not check against the action type and does not perform any
     * additional deserialization - this is left to the child classes to implement.
     * @param json the serialized JSON representation of the Action
     */
    public ActionBase(JSONObject json) {
        try {
            uid = json.getString("uid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Accessor for UID
     * @return String uid
     */
    public String getUID() {
        return uid;
    }
}