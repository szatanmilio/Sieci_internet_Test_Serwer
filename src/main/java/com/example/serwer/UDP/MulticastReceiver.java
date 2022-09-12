package com.example.serwer.UDP;

import java.io.IOException;
import java.net.*;

public class MulticastReceiver extends Thread {
	private int port;
	private String ip;
	private int mainPort;
	private String mainIp;
	private MulticastSocket socket;
	private SocketAddress socketAddress;
	private NetworkInterface nif;
	private boolean isReceiving = false;

	public void setMainData(String ip, int port){
		this.mainIp = ip;
		this.mainPort = port;
	}

	public void initMulticastReceiver(String ip, int port) {
		this.ip = ip;
		this.port = port;
		try {
			this.socket = new MulticastSocket(this.port);
			this.socket.setReuseAddress(true);
			this.socketAddress = new InetSocketAddress(InetAddress.getByName(this.ip), this.port);
			this.nif = NetworkInterface.getByName(this.ip);
			this.socket.joinGroup(this.socketAddress, this.nif);
			this.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void stopReceiver() {
		try {
			this.isReceiving = false;
			this.socket.leaveGroup(this.socketAddress, this.nif);
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void run() {
		byte[] buffer = new byte[1024];
		DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
		this.isReceiving = true;
		while(this.isReceiving) {
			try {
				this.socket.receive(datagramPacket);
				String mess = new String(datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength());
				if(mess.equals("DISCOVER")){
					MulticastSender.send(ip, port, mainIp+":"+mainPort);
				}
			} catch (IOException e) {
				if (!e.getMessage().equalsIgnoreCase("Receive timed out")) {
					e.printStackTrace();
				}
			}
		}
	}
}
