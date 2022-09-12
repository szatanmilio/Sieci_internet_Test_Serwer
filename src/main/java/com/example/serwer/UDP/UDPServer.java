package com.example.serwer.UDP;

import com.example.serwer.MainController;
import javafx.application.Platform;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Arrays;

import static java.lang.Math.abs;

public class UDPServer extends Thread {
	private int port;
	private String ip;
	private DatagramSocket socket;
	byte[] buffer = new byte[25];
	int bufferSize;
	private boolean isReceiving = false;
	private MainController mController;
	private Long bytesRead = 0L;
	private Long counter = 0L;
	private Long missedBytes = 0L;
	byte[] controlBuffer;
	private Long checksum = 0L;

	@Override
	public void run() {
		try {
			InetSocketAddress address = new InetSocketAddress(this.ip, this.port);
			socket = new DatagramSocket(null);
			socket.bind(address);
			socket.setReuseAddress(true);
			DatagramPacket dpReceive = null;
			dpReceive = new DatagramPacket(buffer, buffer.length);
			try {
				socket.receive(dpReceive);
				String received = new String(dpReceive.getData(), 0, dpReceive.getLength());
				String[] firstMsg = received.split(":");
				bufferSize = Integer.parseInt(firstMsg[1]);
				mController.UDPDataSize.setText(String.valueOf(bufferSize));
				isReceiving = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
			controlBuffer = getMessage();
			checksum = getChecksum(controlBuffer);
			Long startTime = System.currentTimeMillis();
			while (isReceiving) {
				buffer = new byte[bufferSize];
				dpReceive = new DatagramPacket(buffer, buffer.length);
				socket.setSoTimeout(1000);
				try {
					counter++;
					socket.receive(dpReceive);

					if(Arrays.toString(buffer).equals("FINE")){
						isReceiving = false;
					} else{
						Long check = getChecksum(buffer);
						if(checksum!=check)
							missedBytes+= abs(checksum - check);

					}
					bytesRead += dpReceive.getLength();
					Platform.runLater(() -> {
						Long statsStartTime = System.currentTimeMillis();
						updateData(startTime, statsStartTime, bufferSize);
					});
					sleep(1);
				} catch (IOException | InterruptedException e) {
					isReceiving = false;
				}
			}
			socket.close();
		} catch (SocketException e) {
			e.printStackTrace();
		}

	}

	private Long getChecksum(byte[] bytes) {
		Long sum = 0L;
		for (byte b : bytes)
			sum += b;
		return sum;
	}


	public void setReceiver(String ip, int port, MainController mController) {
		this.ip = ip;
		this.port = port;
		this.mController = mController;
	}

	public void stopReceiver() {
		this.isReceiving = false;
	}

	private void updateData(Long startTime, Long statsStartTime, int bufferSize) {
		long time = System.currentTimeMillis() - startTime;
		mController.UDPTotalSize.setText(String.valueOf(bytesRead / 1000));
		mController.UDPTime.setText(String.valueOf(time / 1000));
		if (time / 1000 > 0)
			mController.UDPSpeed.setText(String.valueOf((bytesRead / 1000) / (time / 1000)));
		mController.UDPLostData.setText(String.valueOf(missedBytes));
		if(((counter * bufferSize) - bytesRead) == 0)
			mController.UDPError.setText("0");
		else
			mController.UDPError.setText(String.valueOf((counter * bufferSize) / bytesRead));
		mController.UDPCalcTime.setText(String.valueOf(System.currentTimeMillis() - statsStartTime));

	}

	private byte[] getMessage() {
		byte[] buffer = new byte[bufferSize];
		try{
			InputStream is = new FileInputStream("message.txt");
			int readed = is.read(buffer);
			is.close();
		}catch(IOException ioe){
			System.out.println("Error "+ioe.getMessage());
		}
		return buffer;
	}
}
