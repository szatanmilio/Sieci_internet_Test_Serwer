package com.example.serwer;

import com.example.serwer.TCP.TCPServer;
import com.example.serwer.UDP.MulticastReceiver;
import com.example.serwer.UDP.MulticastSender;
import com.example.serwer.UDP.UDPServer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class MainController {
	MulticastReceiver multicastReceiver;
	TCPServer tcpServer;
	UDPServer udpServer;
	String ip = "127.0.0.1";
	String multicastIp = "224.0.0.10";
	int multicastPort = 9999;

	@FXML
	public Button buttonStart;
	public TextField textFieldPort;
	public Label labelTCP;
	public Label labelUDP;
	public TextField TCPDataSize;
	public TextField UDPDataSize;
	public TextField TCPTotalSize;
	public TextField TCPTime;
	public TextField TCPCalcTime;
	public TextField TCPSpeed;
	public TextField TCPLostData;
	public TextField TCPError;
	public TextField UDPTotalSize;
	public TextField UDPTime;
	public TextField UDPCalcTime;
	public TextField UDPSpeed;
	public TextField UDPLostData;
	public TextField UDPError;


	public void initMulticastReceiver() {
		this.multicastReceiver = new MulticastReceiver();
		this.multicastReceiver.setMainData(this.ip ,Integer.parseInt(textFieldPort.getText()));
		textFieldPort.textProperty().addListener((observable -> {multicastReceiver.setMainData(this.ip ,Integer.parseInt(textFieldPort.getText()));}));
		multicastReceiver.initMulticastReceiver(multicastIp, multicastPort);
	}

	public void onStartButtonClick() {
		if(buttonStart.getText().equals("START")){
			cleanGUI();
			tcpServer = new TCPServer(this, this.ip ,Integer.parseInt(textFieldPort.getText()));
			tcpServer.start();
			udpServer = new UDPServer();
			udpServer.setReceiver(this.ip, Integer.parseInt(textFieldPort.getText()),this);
			udpServer.start();
			textFieldPort.setDisable(true);
			buttonStart.setText("STOP");
			labelTCP.setTextFill(Color.web("GREEN"));
			labelUDP.setTextFill(Color.web("GREEN"));
		}
		else{
			MulticastSender.send(multicastIp, multicastPort, "quit");
			tcpServer.stopServer();
			udpServer.stopReceiver();
			textFieldPort.setDisable(false);
			buttonStart.setText("START");
			labelTCP.setTextFill(Color.web("RED"));
			labelUDP.setTextFill(Color.web("RED"));
		}

	}

	private void cleanGUI() {
		UDPTotalSize.setText("0");
		UDPTime.setText("0");
		UDPSpeed.setText("0");
		UDPLostData.setText("0");
		UDPError.setText("0");
		UDPError.setText("0");
		UDPCalcTime.setText("0");
		TCPTotalSize.setText("0");
		TCPTime.setText("0");
		TCPSpeed.setText("0");
		TCPLostData.setText("0");
		TCPError.setText("0");
		TCPError.setText("0");
		TCPCalcTime.setText("0");
	}


}