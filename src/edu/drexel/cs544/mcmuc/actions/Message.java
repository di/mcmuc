package edu.drexel.cs544.mcmuc.actions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONException;
import org.json.JSONObject;

import edu.drexel.cs544.mcmuc.channels.Channel;
import edu.drexel.cs544.mcmuc.channels.Controller;
import edu.drexel.cs544.mcmuc.channels.Room;
import edu.drexel.cs544.mcmuc.util.Certificate;
import edu.drexel.cs544.mcmuc.util.JSON;

/**
 * A chat room message action must carry a "from" field, which is a nickname that identifies
 * the sender, and a "body" field that carries the user's input. Messages can also optionally
 * contain a "to" value, for which clients can choose to hide from the user messages not
 * meant for them. Messages can also be optionally encrypted with the public key certificate
 * specified by "key". Using "to" and "key" together allows clients to avoid checking the "key"
 * against their own keystore, as they will know the messages was not directed at them.
 * 
 * The possible JSON formats are:
 * {'action':'message','from':'<from>','body':'<body>'}
 * {'action':'message','from':'<from>','body':'<body>','to','<to>'}
 * {'action':'message','from':'<from>','body':'<body>','key','<key>'}
 * {'action':'message','from':'<from>','body':'<body>','to','<to>','key','<key>'}
 * 
 */
public class Message extends Action implements JSON {

    public static final String action = "message";
    private String from;
    private String body;
    private String to;
    private Boolean hasTo;
    private Certificate key;
    private Boolean hasKey;

    /**
     * Initializes the Message class. If either to or key are null, then hasTo() or hasKey() will return
     * false, respectively.
     * 
     * @param from String from
     * @param body String body
     * @param to String to
     * @param key Certificate key
     */
    private void init(String from, String body, String to, Certificate key, boolean encrypt) {
        this.from = from;
        this.body = body;
        if (to != null) {
            this.to = to;
            this.hasTo = true;
        } else
            this.hasTo = false;
        if (key != null) {
            this.key = key;
            this.hasKey = true;
            if(encrypt)
            	encryptBody(key);
        } else
            this.hasKey = false;
    }

    /**
     * Calls init() to create message with a from and body but not to or certificate
     * 
     * @param from String from
     * @param body String body
     */
    public Message(String from, String body) {
        init(from, body, null, null, false);
    }

    /**
     * Calls init() to create message with a from, body, and to but no certificate
     * 
     * @param from String from
     * @param body String body
     * @param to String to
     */
    public Message(String from, String body, String to) {
        init(from, body, to, null, false);
    }

    /**
     * Calls init() to create message with a from, body, and certificate but no to
     * 
     * @param from String from
     * @param body String body
     * @param key Certificate key
     */
    public Message(String from, String body, Certificate key) {
        init(from, body, null, key, true);
    }

    /**
     * Calls init() to create message with a from, body, to, and certificate
     * 
     * @param from String from
     * @param body String body
     * @param to String to
     * @param key Certificate key
     */
    public Message(String from, String body, String to, Certificate key) {
        init(from, body, to, key, true);
    }

    /**
     * Decrypts the body of the message, using the provided private key certificate of the user
     * that received the message. Only X.509 certificates using RSA privates keys are currently supported.
     * 
     * @param Private Certificate for the receiver of the message.
     */
    public void decryptBody(Certificate PrivateKey) {
        InputStream inStream = new ByteArrayInputStream(PrivateKey.getCertificate());
        try {
            byte[] keyBytes = new byte[inStream.available()];
            inStream.read(keyBytes);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] textBytes = cipher.doFinal(Certificate.stringToBytes(body));
            this.body = new String(textBytes);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

    }

    /**
     * Encrypts the body of the message, using the provided public key certificate of the intended
     * recipient. Only X.509 certificates using RSA public keys are currently supported.
     * 
     * @param PublicKey Certificate for the message receipent's public key
     */
    public void encryptBody(Certificate PublicKey) {
        InputStream inStream = new ByteArrayInputStream(PublicKey.getCertificate());
        CertificateFactory cf;
        try {
            cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);
            RSAPublicKey key = (RSAPublicKey) cert.getPublicKey();

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] cipherText = cipher.doFinal(this.body.getBytes());
            this.body = Certificate.bytesToString(cipherText);

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deserializes JSON into a Message object. If the JSON has to or key key/value pairs, the object's
     * to or key attributes are filled in and hasTo() or hasKey() return true; otherwise they return false.
     * 
     * @param json the JSONObject
     */
    public Message(JSONObject json) {
        super(json, Message.action);
        try {
            String from = json.getString("from");
            String body = json.getString("body");

            String to = null;
            Certificate key = null;

            if (json.has("to"))
                to = json.getString("to");

            if (json.has("key"))
                key = new Certificate(json.getJSONObject("key"));

            init(from, body, to, key, false);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Accessor for message's from field
     * 
     * @return String from
     */
    public String getFrom() {
        return from;
    }

    /**
     * Accessor for message's body field
     * 
     * @return String body
     */
    public String getBody() {
        return body;
    }

    /**
     * Accessor for message's (optional) to field
     * 
     * @return String to (null if hasTo() returns false)
     */
    public String getTo() {
        return to;
    }

    /**
     * Determine if message's optional to field is filled
     * 
     * @return true if message has 'to' address, false otherwise
     */
    public Boolean hasTo() {
        return hasTo;
    }

    /**
     * Accessor for message's Certificate key field
     * 
     * @return Certificate key (null if hasKey() returns false)
     */
    public Certificate getKey() {
        return key;
    }

    /**
     * Determine if message's optional key field is filled
     * 
     * @return true if message has Certificate key, false otherwise
     */
    public Boolean hasKey() {
        return hasKey;
    }

    /**
     * Serializes the Message object to JSON. The to and key key/value pairs are only included if
     * the object was created with a to or key attribute.
     */
    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {
            json.put("action", "message");
            json.put("uid", uid);
            json.put("from", from);
            json.put("body", body);
            if (hasTo)
                json.put("to", to);
            if (hasKey)
                json.put("key", key.toJSON());
        } catch (JSONException e) {

        }

        return json;
    }

    /**
     * Display the message to the user and forward it on the channel
     */
    public void process(Channel channel) {
        class Runner implements Runnable {
            Message message;
            Channel channel;

            Runner(Message m, Channel c) {
                channel = c;
                message = m;
            }

            public void run() {
                if (message.hasKey()) {
                    Room r = (Room) channel;
                    Certificate privateKey = r.getKeyPairs().get(message.getKey());
                    if (privateKey != null)
                        message.decryptBody(privateKey);
                }
                if (message.hasTo()) {
                    Room r = (Room) channel;
                    if (r.getUserName().equals(message.getTo()))
                        Controller.getInstance().output(message.getFrom() + "@" + ((Room) channel).getName() + " (private): " + message.getBody() + " (" + message.getUID() + ")");
                } else
                    Controller.getInstance().output(message.getFrom() + "@" + ((Room) channel).getName() + ": " + message.getBody() + " (" + message.getUID() + ")");
                channel.send(message);
            }
        }
        Thread t = new Thread(new Runner(this, channel));
        t.start();
    }
}