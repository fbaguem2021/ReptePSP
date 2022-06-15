package com.example.application;

import com.example.application.classes.MySocket;
import com.example.application.classes.ReadM;
import com.example.application.models.Response;
import com.example.application.models.User;

import java.io.IOException;

import static com.example.application.models.Actions.*;

public class Admin {
    public static final void separador(){System.out.println("==================================================");}
    public static void menu(MySocket socket, User user) {
        boolean sortir = false;
        int opcion;
        do {
            separador();
            System.out.println("Que quieres hacer?\n" +
                    "1- Crear espectaculo\n" +
                    "2- Ver espectaculo\n" +
                    "0- Salir");
            do {
                opcion = ReadM._int("Opcion: ");
            } while (opcion < 0 || opcion > 2);

            switch (opcion) {
                case 1:
                    crearEspectaculo(socket);
                    break;
                case 2:
                    verEspectaculos(socket);
                    break;
                case 0:
                    separador();
                    sortir = true;
                    System.out.println("Saliendo");
                    break;
            }
        } while (!sortir);
    }
    private static void crearEspectaculo(MySocket socket) {
        try {
            separador();
            String nombre = ReadM._String("Nombre: ");
            Response res = new Response();
            res.espectaculo = nombre;
            res.action = ESPECTACULO_CREAR_INTENTO;
            socket.send((Object) res);

            Response respuesta = (Response) socket.readObject();
            if (respuesta.action == ESPECTACULO_CREAR_CORRECTO) {
                System.out.println("Espectaculo creado correctamente");
            } else {
                System.out.println(respuesta.message);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ha ocurrido un error");
			e.printStackTrace();
        }
    }
    private static void verEspectaculos(MySocket socket) {
        try {
            separador();
            socket.send((Object) new Response(ESPECTACULOS_OBTENER));
            Response res = (Response) socket.readObject();
            if (res.action == ESPECTACULOS_OBTENER_CORRECTO) {
                res.espectaculos.forEach(System.out::println);
            } else if (res.action == ESPECTACULOS_OBTENER_ERROR) {
                System.out.println("Ha ocurrido un error mientras se intentava obtener los espectaculos");
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
