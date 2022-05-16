package com.example.application;

import com.example.application.classes.MySocket;
import com.example.application.classes.MyThread;
import com.example.application.classes.MyThread.setOnThreadRunArgs;
import com.example.application.models.Actions;
import com.example.application.models.Response;
import jdk.swing.interop.SwingInterOpUtils;

import java.io.IOException;

/**
 *
 * @author francesc
 */
public class Main {

    public static void main(String[] args) {
        int i = 0;
        while(true) {
            final MySocket socket = new MySocket(5000+i);
            try {
                socket.accept();
                lunchThread(socket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            i++;
        }
    }
    public static void lunchThread(MySocket socket) {
        new MyThread().startThread( () -> {
            try {
                socketMenu(socket);
                socket.close();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }
    // Metode que funciona de menu y que mitgançant un switch i una classe enum,
    // detecta quina es l'accio que realitza l'usuari
    public static void socketMenu(MySocket socket) throws IOException, ClassNotFoundException {
        boolean sortir = false;
        do {
            Response response = (Response) socket.readObject();
            switch (response.action) {
                case LOGIN_ADMIN_INTENTO -> {}
                case LOGIN_CLIENTE_INTENTO -> {}
                case USUARIO_ALTA_INTENTO -> {}
                case USUARIO_BAJA_INTENTO -> {}
                case USUARIO_MODIFICAR_INTENTO -> {}
                case ENTRADAS_VER_ESTADO -> {}
                case ENTRADAS_ANULAR_MOSTRAR_DISPONIBLES -> {}
                case ENTRADAS_ANULAR_INTENTO -> {}
                case ENTRADAS_RESERVAR_MOSTRAR_DISPONIBLES -> {}
                case ENTRADAS_RESERVAR_INTENTO -> {}
                case LOGIN_CERRAR_SESSION -> {
                    socket.send(new Response(Actions.LOGIN_CERRAR_SESSION_CORRECTO));
                    sortir = true;
                }
            }
        } while (sortir);
    }
}
