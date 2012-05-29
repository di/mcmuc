package edu.drexel.cs544.mcmuc;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * DuplicateDetector is a ConcurrentLimitedLinkedQueue (CLLQ) of string-based unique identifiers,
 * limited in size to 200 members
 * 
 * For the multicast flooding algorithm, each node must have a way of tracking of each  message
 * it has forwarded, to avoid forwarding them again. Simply requiring the clients to keep 
 * the last n messages is insufficient, as there is no method to distinguish between 
 * duplicates and messages that are new but the same as old messages (such as when a room 
 * gets re-created or a message with the same body/from fields). Thus the protocol will 
 * require unique message ID numbers; each client can generate a unique ID for each message 
 * it creates, and every other client can use this ID to determine if the message is a 
 * duplicate or not.
 * 
 * @see ConcurrentLimitedLinkedQueue
 */
public class DuplicateDetector {
    private static final DuplicateDetector instance = new DuplicateDetector();

    private ConcurrentLimitedLinkedQueue<String> q;
    private int size = 200;

    /**
     * Create a CLLQ of strings of size 200
     */
    private DuplicateDetector() {
        q = new ConcurrentLimitedLinkedQueue<String>(size);
    }
    
    /**
     * A message's hash is the concatenation of its unique identifer (uid) and the MD5
     * hash of its JSON serialization
     * @param jo Action serialized into JSON to be hashed
     * @return String the hash
     * @throws UnsupportedEncodingException 
     */
    private String getMessageHash(JSONObject jo)
    {
    	String hash = null;
    	try {
    		hash = jo.getString("uid") + MD5(jo.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return hash;
    }
    
    /**
     * Gets the hex representation of a MD5 hash of a given string
     * @param md5 String to hash
     * @return the hash as string of hex characters (0-9,A-F)
     */
    private String MD5(String md5) {
    	   try {
    	        MessageDigest md = MessageDigest.getInstance("MD5");
    	        byte[] array = md.digest(md5.getBytes());
    	        StringBuffer sb = new StringBuffer();
    	        for (int i = 0; i < array.length; ++i) {
    	          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
    	       }
    	        return sb.toString();
    	    } catch (java.security.NoSuchAlgorithmException e) {
    	    	e.printStackTrace();
    	    }
    	    return null;
    	}

    /**
     * If the hash is already in the CLLQ then the message is a duplicate that has already been
     * forwarded, and must not be forwarded further
     * @param uid String unique identifier
     * @return true if previously unseen hash, false otherwise
     */
    public boolean isDuplicate(JSONObject jo) {
        if (q.contains(getMessageHash(jo))) {
		    return true;
		} else {
		    return false;
		}
    }

    /**
     * Add a seen message's hash
     * @param uid String hash
     * @return true if hash was added to the CLLQ, false otherwise
     */
    public boolean add(JSONObject jo) {
        return q.add(getMessageHash(jo));
    }

    /**
     * Restricts the instantiation of the DuplicateDectector class to one object (the Singleton
     * design pattern)
     * @return DuplicateDetector singleton
     */
    public static DuplicateDetector getInstance() {
        return instance;
    }
}
