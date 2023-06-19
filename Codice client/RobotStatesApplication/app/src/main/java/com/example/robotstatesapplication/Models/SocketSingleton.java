package com.example.robotstatesapplication.Models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketSingleton {

    private static SocketSingleton instance;

    private Socket appSocket;
    private PrintWriter socketOut;
    private BufferedReader socketIn;

    private SocketSingleton() {
        try {
            appSocket = new Socket("192.168.1.104", 5000);
            socketOut = new PrintWriter(appSocket.getOutputStream());
            socketIn = new BufferedReader(new InputStreamReader(appSocket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static SocketSingleton getInstance() {
        if (instance == null)
            instance = new SocketSingleton();
        return instance;
    }

    public static void rinnovaIstanza() {
        instance = new SocketSingleton();
    }

    public Socket getAppSocket() {
        return appSocket;
    }

    public PrintWriter getSocketOut() {
        return socketOut;
    }

    public BufferedReader getSocketIn() {
        return socketIn;
    }

}
