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
import java.net.UnknownHostException;


/**
 * Client
 * @author Francesc Bagué Martí
 */
public class Main {

    public static final void separador(){System.out.println("==================================================");}
    public static void main(String[] args) {
        String IP = ReadM._String("IP del servidor: ");
        final int PORT  = 5000;//ReadM._int("Puerto: ");
        int i = 0;
        boolean porterr;
        try {
            MySocket socket = new MySocket(IP, PORT+i);
            do {
                porterr = false;
                try {
                    socket.start();
                } catch (UnknownHostException ex) {
                    System.out.println("Ip no encontrada. Introduce otra direccion ip");
                    IP = ReadM._String("IP: ");
                    socket = new MySocket(IP, PORT+i);
                } catch (Exception ex) {
                    i++;
                    porterr = true;
                    socket = new MySocket(IP, PORT+i);
                }
                
            } while (porterr);
            menuInicial(socket);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
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
                    cerrarAplicacion(socket);
                    System.out.println("Saliendo");
                    break;
            }

        } while (!salir);
    }

    private static void cerrarAplicacion(MySocket socket) {
        try {
            socket.send((Object) new Response(APP_CERRAR));
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void crearUsuario(MySocket socket) {
        try {
            separador();
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
			e.printStackTrace();
        }

    }

    private static void login(MySocket socket) {
        try {
            separador();
            Response response = new Response();
            Response resultado;
            String username = ReadM._String("Nombre de usuario: ");
            String contrasena = ReadM._String("Contraseña: ");
            if (username.equals("administrador")) {
                response.action = LOGIN_ADMIN_INTENTO;
            } else {
                response.action = LOGIN_CLIENTE_INTENTO;
            }
            response.user = new User(username, contrasena);
            //response.user = new User("administrador", "a");
            socket.send((Object) response);
            resultado = (Response) socket.readObject();

            switch (resultado.action) {
                case LOGIN_ADMIN_CORRECTO:
                    System.out.println("Inicio de sesion de administrador correcto");
                    Admin.menu(socket, resultado.user);
                    break;
                case LOGIN_ADMIN_INCORRECTO:
                case LOGIN_CLIENTE_INCORRECTO:
                    System.out.println("Usuario, i/o contraseña incorrectos. Regresando al menu principal");
                    break;
                case LOGIN_CLIENTE_CORRECTO:
                    System.out.println("Inicio de sesion de cliente correcto");
                    Client.menu(socket, resultado.user);
                break;
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
