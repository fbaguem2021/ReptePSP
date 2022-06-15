package com.example.application.classes;

import java.io.IOException;

import com.example.application.Main;

public class CustomRunnable implements Runnable {
    private MySocket socket;
    public CustomRunnable(MySocket socket) {
        this.socket = socket;
    }
    public CustomRunnable(){}
    @Override
    public void run() {
        try {
            Main.socketMenu(Main.socket);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
}
