package com.example.application;

import com.example.application.classes.MySocket;
import com.example.application.classes.ReadM;
import static com.example.application.models.Actions.*;
import com.example.application.models.Response;
import com.example.application.models.User;
import com.example.application.processes.Espectacles;
import com.example.application.processes.UsuarioModificar;

import java.io.IOException;

public class Client {
    public static void menu(MySocket socket, User user) {
        boolean sortir = false;
        int opcion;
        do {
            System.out.println("Que quieres hacer:\n  " +
                    "1- Comprar entradas\n  " +
                    "2- Devolver entradas\n  " +
                    "3- Gestionar datos de usuario\n  " +
                    "0- Cerrar sesión");
            do {
                opcion = ReadM._int("Opcion: ");
            } while (opcion < 0 || opcion > 2);

            switch (opcion) {
                case 1:
                    Espectacles.menu(socket, user, Espectacles.MODO_RESERVA);
                    break;
                case 2:
                    Espectacles.menu(socket, user, Espectacles.MODO_ANULACION);
                    break;
                case 3:
                    user = UsuarioModificar.modificar(socket, user);
                    break;
                case 0:
                    sortir = true;
                    break;
            }
        } while (!sortir);
    }
}
