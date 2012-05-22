package edu.drexel.cs544.mcmuc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Room {

    int multicastPort;
    MulticastSocket multicastSocket;
    InetAddress multicastAddress;

    public Room(String name, int portsInUse[]) {
        this.multicastPort = choosePort(name, portsInUse);
        try {
            this.multicastSocket = new MulticastSocket(this.multicastPort);
            this.multicastAddress = InetAddress.getByName("224.5.4.4");
            this.multicastSocket.joinGroup(multicastAddress);
        } catch (SocketException e) {
        	System.err.println("Error: Multicast route likely does not exist. Add using (for example):\n" +
        			"\t$ ip route add 224.0.0.0/4 dev eth0");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MulticastReceiveRunnable runner = new MulticastReceiveRunnable(this);
        Thread runnerThread = new Thread(runner);
        runnerThread.start();
    }

    public int choosePort(String name, int portsInUse[]) {
    	int f = -1;
    	
    	try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
			
			byte[] md5_digest = md5.digest(name.getBytes());
			byte[] sha1_digest = sha1.digest(name.getBytes());
			
			int g = ByteBuffer.wrap(md5_digest,0,4).getInt();
			int h = ByteBuffer.wrap(sha1_digest,0,4).getInt();
			
			int i = 1;
			do
			{
				f = ((g + i * h) % 16382) + 49152;
				i++;
			}
			while(Arrays.asList(portsInUse).contains(f));

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
    	
        return f;
    }

    public void send(String message) {
        System.out.println("Sending: " + message);
        DatagramPacket dp = new DatagramPacket(message.getBytes(), message.length(), this.multicastAddress, this.multicastPort);
        try {
            this.multicastSocket.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MulticastSocket getMulticastSocket() {
        return this.multicastSocket;
    }

    public void receive(String s) {
        System.out.println("Got:\"" + s + "\"");
    }
}
