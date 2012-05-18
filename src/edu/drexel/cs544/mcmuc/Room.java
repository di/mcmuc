package edu.drexel.cs544.mcmuc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Room {

    int multicastPort;
    MulticastSocket multicastSocket;
    InetAddress multicastAddress;

    public Room(String name) {
        this.multicastPort = choosePort(name);
        try {
            this.multicastSocket = new MulticastSocket(this.multicastPort);
            this.multicastAddress = InetAddress.getByName("224.5.4.4");
            this.multicastSocket.joinGroup(multicastAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MulticastReceiveRunnable runner = new MulticastReceiveRunnable(this);
        runner.run();
    }

    public int choosePort(String name) {
        return 54321;
    }

    public void send(String message) {
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
