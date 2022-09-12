package com.example.serwer.UDP;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class MulticastSender {
	public MulticastSender() {
	}

	public static void send(String ip, int port, String mess){
		try{
			MulticastSocket socket = new MulticastSocket();
			byte[] buffer = mess.getBytes(StandardCharsets.UTF_8);
			DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), port);
			socket.send(datagramPacket);
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
