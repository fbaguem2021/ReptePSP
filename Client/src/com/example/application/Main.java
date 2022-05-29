/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.example.application;

import static com.example.application.models.Actions.*;
import com.example.application.classes.*;
import com.example.application.models.*;
import com.example.application.processes.UsuarioCrear;

import java.io.IOException;


/**
 *
 * @author francesc
 */
public class Main {

    public static final void separador(){System.out.println("==================================================");}
    public static void main(String[] args) {
        final String IP = ReadM._String("IP del servidor: ");
        final int PORT  = ReadM._int("Puerto (de 5000 a arriva): ");

        MySocket socket = new MySocket(IP, PORT);
        try {
            socket.start();
            menuInicial(socket);
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void menuInicial(MySocket socket) {
        boolean salir = false;
        int opcion;
        do {
            separador();
            System.out.println("Que quieres hacer?");
            System.out.println("" +
                    "1- Iniciar session\n" +
                    "2- Registrar\n" +
                    "0- Salir");
            do {
                opcion = ReadM._int("Opcion: ");
            } while (opcion < 0 || opcion > 2);

            switch (opcion) {
                case 1:
                    login(socket);
                    break;
                case 2:
                    crearUsuario(socket);
                    break;
                case 0:
                    salir = true;
                    System.out.println("Saliendo");
                    break;
            }

        } while (!salir);

    }

    private static void crearUsuario(MySocket socket) {
        try {
            User usuario = UsuarioCrear.crear();
            Response res = new Response();
            res.action = USUARIO_ALTA_INTENTO;
            res.user = usuario;
            socket.send((Object) res);
            Response respuesta = (Response) socket.readObject();
            if (respuesta.action.equals(USUARIO_ALTA_CORRECTO)) {
                System.out.println("Usuario creado correctamente");
            } else if (respuesta.action.equals(USUARIO_ALTA_INCORRECTO)) {
                System.out.println(res.message);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ha ocurrido un error mientras se intentava añadir el usuario");
        }

    }

    private static void login(MySocket socket) {
        try {
            separador();
            Response response = new Response();
            Response resultado;
            String username = ReadM._String("Nombre de usuario: ");
            String contrasena = ReadM._String("Contraseña");
            if (username.equals("administrador")) {
                response.action = LOGIN_ADMIN_INTENTO;
            } else {
                response.action = LOGIN_CLIENTE_INTENTO;
            }
            response.user = new User(username, contrasena);
            socket.send((Object) response);
            resultado = (Response) socket.readObject();

            switch (resultado.action) {
                case LOGIN_ADMIN_CORRECTO -> {
                    System.out.println("Inicio de sesion de administrador correcto");
                    Admin.menu(socket, resultado.user);
                }
                case LOGIN_ADMIN_INCORRECTO, LOGIN_CLIENTE_INCORRECTO -> {
                    System.out.println("Usuario, i/o contraseña incorrectos. Regresando al menu principal");
                }
                case LOGIN_CLIENTE_CORRECTO -> {
                    System.out.println("Inicio de sesion de cliente correcto");
                    Client.menu(socket, resultado.user);
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.toString());
        }
    }
}
