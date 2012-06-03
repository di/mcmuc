package edu.drexel.cs544.mcmuc.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.channels.Channel;
import edu.drexel.cs544.mcmuc.channels.Controller;
import edu.drexel.cs544.mcmuc.util.Certificate;
import edu.drexel.cs544.mcmuc.util.JSON;

/**
 * Presence is used by each client to respond to a broadcast PollPresence.
 * 
 * The JSON format of a Presence is:
 * {'action':'presence','from':'<from>','status':'<status>'} if no public-keys are included or
 * {'action':'presence','from':'<from>','status':'<status>','keys':[<keys>]} if they are.
 */
public class Presence extends Action implements JSON {
    public static final String action = "presence";

    private String from;
    private Status status;
    private List<Certificate> keys;

    public enum Status {
        Online, Offline
    }

    /**
     * Initializes the from, status, and keys attributes of the Presence class. Status must be either:
     * 'online' or 'offline'
     * 
     * @param from
     * @param status must be 'online' or 'offline'
     * @param keys
     * @throws Exception thrown if status is not 'online' or 'offline'
     */
    private void init(String from, Status status, List<Certificate> keys) {
        this.from = from;
        this.status = status;
        this.keys = keys;
    }

    /**
     * Calls init with passed-in parameters - exception is forwarded from init. Use if user desires
     * to include public-keys with presence.
     * 
     * @param from
     * @param status
     * @param keys
     * @throws Exception
     */
    public Presence(String from, Status status, List<Certificate> keys) {
        init(from, status, keys);
    }

    /**
     * Calls init with passed-in parameters - exception is forwarded from init. Use if no keys are
     * required to be sent along with presence.
     * 
     * @param from
     * @param status
     * @throws Exception
     */
    public Presence(String from, Status status) {
        init(from, status, null);
    }

    /**
     * Accessor for message's from field
     * 
     * @return
     */
    public String getFrom() {
        return this.from;
    }

    /**
     * Accessor for message's status field
     * 
     * @return
     */
    public Status getStatus() {
        return this.status;
    }

    /**
     * Accessor for message's (optional) list of public-key certificates
     * 
     * @return
     */
    public List<Certificate> getKeys() {
        return this.keys;
    }

    /**
     * Deserializes JSON into a Presence object. The status key must have 'offline' or 'online' as its value.
     * The keys key/value pair is optional.
     * 
     * @param json the JSON to deserialize
     * @throws Exception thrown if status is not 'online' or 'offline'
     */
    public Presence(JSONObject json) throws Exception {
        super(json, Presence.action);

        try {
            this.from = json.getString("from");

            String rawStatus = json.getString("status");
            if (rawStatus.equalsIgnoreCase("online"))
                this.status = Status.Online;
            else if (rawStatus.equalsIgnoreCase("offline"))
                this.status = Status.Offline;
            else
                throw new Exception("Unsupported status");

            if (json.has("keys")) {
                JSONArray keys = json.getJSONArray("keys");
                this.keys = new ArrayList<Certificate>(keys.length());

                if (keys != null) {
                    int len = keys.length();
                    for (int i = 0; i < len; i++) {
                        this.keys.add(new Certificate(keys.getJSONObject(i)));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Serializes Presence object into JSON. keys key/value pair is only set if object was created with
     * a list of keys.
     */
    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        JSONArray list = new JSONArray();

        try {
            json.put("action", Presence.action);
            json.put("uid", uid);
            json.put("from", from);

            if (status == Status.Offline)
                json.put("status", "offline");
            else if (status == Status.Online)
                json.put("status", "online");

            if (keys != null) {
                Iterator<Certificate> itr = keys.iterator();
                while (itr.hasNext()) {
                    list.put(itr.next().toJSON());
                }
                json.put("keys", list);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Upon receiving a Presence from another client, display it to the user. If any keys are
     * included, also display those to the user. If the message is not a duplicate, forward it
     * on the channel.
     */
    @Override
    public void process(Channel channel) {
        class Runner implements Runnable {
            Presence message;
            Channel channel;

            Runner(Presence m, Channel c) {
                channel = c;
                message = m;
            }

            public void run() {
                if (message.getStatus() == Status.Online)
                    Controller.getInstance().output(message.getFrom() + " is online " + " (" + message.getUID() + ")");
                else if (message.getStatus() == Status.Offline)
                    Controller.getInstance().output(message.getFrom() + " is offline " + " (" + message.getUID() + ")");

                List<Certificate> keys = message.getKeys();
                if (keys != null) {
                    Iterator<Certificate> it = keys.iterator();
                    while (it.hasNext()) {
                        Certificate c = it.next();
                        Controller.getInstance().output("Advertised " + c.getFormat() + " public-key cert from " + message.getFrom() + ":\n\t" + c.getCertificate());
                    }
                }

                channel.send(message);
            }
        }
        Thread t = new Thread(new Runner(this, channel));
        t.start();
    }
}