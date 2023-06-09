package com.example.robotstatesapplication.Views;

import android.app.Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RobotStatesApplication extends Application {

    Socket appSocket;
    PrintWriter socketOut;
    BufferedReader socketIn;

    @Override
    public void onCreate() {
        try {
            appSocket = new Socket("127.0.0.1", 5000);
            socketOut = new PrintWriter(appSocket.getOutputStream());
            socketIn = new BufferedReader(new InputStreamReader(appSocket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.onCreate();
    }

    public PrintWriter getSocketWriter() {
        if (appSocket != null)
            return socketOut;
        else
            return null;
    }

    public BufferedReader getSocketReader() {
        if (appSocket != null)
            return socketIn;
        else
            return null;
    }

}
