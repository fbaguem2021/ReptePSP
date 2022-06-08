package com.example.application.processes;

import com.example.application.classes.*;
import com.example.application.models.*;
import static com.example.application.models.Actions.*;

import java.io.IOException;
import java.util.Scanner;

public class UsuarioModificar {
    public static final void separador(){System.out.println("==================================================");}
    static boolean modificaciones = false;
    static User moddedUser;
    public static User modificar(MySocket socket, User user) {
        separador();
        System.out.println("Deves introducir tu contraseña para accedxer a este menu");
        String contrasenia = ReadM._String("Contraseña: ");
        if (BCrypt.checkpw(contrasenia, user.password)) {
             menu(socket, user);
        } else {
            System.out.println("Contraseña incorrecta");
        }
        if (modificaciones) {
            return moddedUser;
        } else {
            return user;
        }
    }
    private static void menu(MySocket socket, User user){
        moddedUser = user;
        boolean salir = false;
        do {
            separador();
            System.out.println("Que quieres modificar?\n" +
                    "1- Nombre\n" +
                    "2- Apellidos\n" +
                    "3- Nombre de usuario\n" +
                    "4- Email\n" +
                    "5- Contraseña\n" +
                    "6- Numero de telefono\n" +
                    "7- Tarjeta de credito\n" +
                    "0- Salir");
            int opcion = ReadM._int("Opcion: ");
            while ( opcion < 0 || opcion > 7) {
                opcion = ReadM._int(" ----> ");
            }
            switch (opcion) {
                case 1:
                    name(user);
                    break;
                case 2:
                    surname(user);
                    break;
                case 3:
                    username(user);
                    break;
                case 4:
                    email(user);
                    break;
                case 5:
                    password(user);
                    break;
                case 6:
                    phone(user);
                    break;
                case 7:
                    credit(user);
                    break;
                case 0:
                    if (modificaciones) {
                        guardarCanvios(socket, user, moddedUser);
                    }
                    salir = true;
                    break;
            }
        } while (!salir);
    }
    private static void guardarCanvios(MySocket socket, User user, User newUser) {
        try {
            Response res = new Response(USUARIO_MODIFICAR_INTENTO, user, newUser);
            socket.send((Object) res);
            Response resposta = (Response) socket.readObject();
            if (resposta.action == USUARIO_MODIFICAR_CORRECTO) {
                System.out.println("Usuario modificado correctamente");
            } else {
                System.out.println("Los canvios no se han realizado correctamente");
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private static void name(User user) {
        System.out.println("Original: "+user.name);
        String name = ReadM._String("Nuevo: ");
        if (name.equals("")) {
            name    = ReadM._String(" ----> ");
        }
        moddedUser.name = name;
        modificaciones=true;
    }

    private static void surname(User user) {
        System.out.println("Original: "+user.surname);
        String surname = ReadM._String("Nuevo: ");
        if (surname.equals("")) {
            surname    = ReadM._String(" ----> ");
        }
        moddedUser.surname = surname;
        modificaciones=true;
    }

    private static void username(User user) {
        System.out.println("Original: "+user.userName);
        String username = ReadM._String("Nuevo: ");
        if (username.equals("")) {
            username    = ReadM._String(" ----> ");
        }
        moddedUser.userName = username;
        modificaciones=true;
    }

    private static void email(User user) {
        System.out.println("Original: "+user.email);
        String email = ReadM._String("Nuevo: ");
        if (email.equals("")) {
            email    = ReadM._String(" ----> ");
        }
        moddedUser.email = email;
        modificaciones=true;
    }

    private static void password(User user) {
        String password = ReadM._String("Nuevo: ");
        if (password.equals("")) {
            password    = ReadM._String(" ----> ");
        }
        String repeated = ReadM._String("Nuevo: ");
        if (password.equals("")) {
            repeated    = ReadM._String(" ----> ");
        }
        if (repeated.equals(password)) {
            moddedUser.password = BCrypt.hashpw(password, BCrypt.gensalt(10));
            modificaciones=true;
        }
    }

    private static void phone(User user) {
        System.out.println("Original: "+user.phone);
        String phone    = ReadM._String("Nuevo: ");
        try { Integer.parseInt(phone); }
        catch (Exception e) { phone=""; }
        while (phone.equals("")) {
            phone       = ReadM._String(" ----> ");
            if (!phone.equals("")) {
                try { Integer.parseInt(phone); } catch (Exception e) { phone=""; }
            }
        }
        moddedUser.phone = phone;
        modificaciones=true;
    }

    private static void credit(User user) {
        System.out.println("Original: "+user.tarjetaCredito);
        String credit    = ReadM._String("Nuevo: ");
        try { Integer.parseInt(credit); }
        catch (Exception e) { credit=""; }
        while (credit.equals("")) {
            credit       = ReadM._String(" ----> ");
            if (!credit.equals("")) {
                try { Integer.parseInt(credit); } catch (Exception e) { credit=""; }
            }
        }
        moddedUser.tarjetaCredito = credit;
        modificaciones=true;
    }
}
