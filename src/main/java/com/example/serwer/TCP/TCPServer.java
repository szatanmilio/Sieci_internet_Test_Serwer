package com.example.serwer.TCP;

import com.example.serwer.MainController;
import javafx.application.Platform;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer extends Thread {
    MainController mController;
    ServerSocket ss;
    private String ip;
    private int port;
    private boolean isFull = false;
    private boolean isRunning = true;
    private Long bytesRead = 0L;
    private Long counter = 0L;

    public TCPServer(MainController mController, String ip, int port) {
        this.mController = mController;
        this.ip = ip;
        this.port = port;
    }

    public TCPServer(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void run() {
        ss = null;
        try {
            ss = new ServerSocket();
            ss.bind(new InetSocketAddress(this.ip, getPort()));
            while (isRunning)
                new ClientHandler(ss.accept()).start();
        } catch (IOException e) {
            isRunning = false;
        } finally {
            assert ss != null;
            stop();
        }
    }

    public void stopServer() {
        isRunning = false;
        isFull = false;
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            if (isFull) {
                sendMessageBackToClient("Server is full");
                return;
            }
            isFull = true;
            sendMessageBackToClient("Server is not full");
            try {
                DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
                byte[] controlMessage = new byte[25];
                int bytesReaded = dataInputStream.read(controlMessage);
                String[] firstMsg = (new String(controlMessage, 0, bytesReaded)).split(":");
                int bufferSize = Integer.parseInt(firstMsg[1]);
                byte[] message = new byte[bufferSize];
                mController.TCPDataSize.setText(String.valueOf(bufferSize));
                Long startTime = System.currentTimeMillis();
                while (isRunning) {
                    counter++;
                    getMessageFromClient(message);
                    Platform.runLater(() -> {
                        Long statsStartTime = System.currentTimeMillis();
                        updateData(startTime, statsStartTime, bufferSize);
                    });
                    sleep(1);
                }
                isFull = false;
                clientSocket.close();
                ss.close();
            } catch (IOException e) {
                System.out.println("Something went wrong!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void sendMessageBackToClient(String mess) {
            try {
                OutputStream outputStream = this.clientSocket.getOutputStream();
                DataOutputStream objectOutputStream = new DataOutputStream(outputStream);
                objectOutputStream.write(mess.getBytes());
            } catch (IOException e) {
                System.out.println("Something went wrong!");
            }
        }

        private void getMessageFromClient(byte[] message) {
            try {
                InputStream inputStream = this.clientSocket.getInputStream();
                if (inputStream.read(message) == -1) {
                    isRunning = false;
                    this.clientSocket.close();
                    return;
                }
                bytesRead += inputStream.read(message);
            } catch (IOException e) {
                e.printStackTrace();
                isRunning = false;
            }
        }

        private void updateData(Long startTime, Long statsStartTime, int bufferSize) {
            long time = System.currentTimeMillis() - startTime;
            mController.TCPTotalSize.setText(String.valueOf(bytesRead / 1000));
            mController.TCPTime.setText(String.valueOf(time / 1000));
            if (time / 1000 > 0)
                mController.TCPSpeed.setText(String.valueOf((bytesRead / 1000) / (time / 1000)));
            mController.TCPLostData.setText(String.valueOf((counter * bufferSize) - bytesRead));
            if(((counter * bufferSize) - bytesRead) == 0)
                mController.TCPError.setText("0");
            else
                mController.TCPError.setText(String.valueOf(((counter * bufferSize) / bytesRead)));
            mController.TCPCalcTime.setText(String.valueOf(System.currentTimeMillis() - statsStartTime));
        }
    }
}
