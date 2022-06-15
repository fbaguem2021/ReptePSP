package com.example.application.classes;

import java.io.EOFException;
import java.io.IOException;

import com.example.application.Main;

public class CustomRunnable implements Runnable {
    public static final void separador(){System.out.println("==================================================");}
    private MySocket socket;
    public CustomRunnable(MySocket socket) {
        this.socket = socket;
    }
    public CustomRunnable(){}
    @Override
    public void run() {
        try {
            Main.socketMenu(Main.socket);
        } catch (EOFException ex) {
            System.out.println("\u001B[31m"+"Desconexión de cliente inesperada"+"\u001B[0m");
            separador();
            //ex.printStackTrace();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
}
