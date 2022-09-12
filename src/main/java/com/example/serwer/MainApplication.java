package com.example.serwer;

import com.example.serwer.UDP.MulticastReceiver;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-window.fxml"));
		Scene scene = new Scene(fxmlLoader.load(), 600, 400);
		stage.setTitle("TCP vs UDP server");
		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();
		MainController mainController = fxmlLoader.getController();
		mainController.initMulticastReceiver();
	}

	public static void main(String[] args) {
		launch();
	}
}