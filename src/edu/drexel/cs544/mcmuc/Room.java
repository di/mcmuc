package edu.drexel.cs544.mcmuc;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.actions.Action;
import edu.drexel.cs544.mcmuc.actions.Message;

public class Room extends Channel {

    public Room(String name, Set<Integer> portsInUse) {
        super(choosePort(name, portsInUse));
        MulticastReceiveRunnable runner = new MulticastReceiveRunnable(this);
        Thread runnerThread = new Thread(runner);
        runnerThread.start();
    }

    public static int choosePort(String name, Set<Integer> portsInUse) {
        int f = -1;

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

            byte[] md5_digest = md5.digest(name.getBytes());
            byte[] sha1_digest = sha1.digest(name.getBytes());

            int g = ByteBuffer.wrap(md5_digest, 0, 4).getInt();
            int h = ByteBuffer.wrap(sha1_digest, 0, 4).getInt();

            int i = 1;
            do {
                f = ((g + i * h) % 16382) + 49152;
                i++;
            } while (portsInUse.contains(f));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return f;
    }

    @Override
    public void handleNewMessage(JSONObject jo) {
        Action action;
        try {
            if (jo.getString("action").equalsIgnoreCase(Message.action)) {
                action = new Message(jo);
                action.process(this);
            } else {
                System.err.println("Message action type not supported: " + jo.getString("action"));

            }
        } catch (JSONException e) {
            System.err.println("Message does not have action.");
            e.printStackTrace();
        }
    }
}
