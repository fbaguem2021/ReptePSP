package com.example.application;

import com.example.application.classes.*;
import com.example.application.classes.MyThread.setOnThreadRunArgs;
import com.example.application.models.Actions;
import com.example.application.models.Response;
import com.example.application.models.User;
import jdk.swing.interop.SwingInterOpUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author francesc
 */
public class Main {
    public static final String FILE_FOLDER       = "files"+ File.separator;
    public static final String FILE_USERS        = "files"+ File.separator+"users.txt";
    public static final String FILE_ESPECTACLEs  = "files"+ File.separator+"espectacles.txt";

    public static void main(String[] args) {
        int i = 0;
        while(true) {
            final MySocket socket = new MySocket(5000+i);
            System.out.println("Esperando conexiones");
            try {
                socket.accept();
                System.out.println("conexión iniciada");
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
                case LOGIN_ADMIN_INTENTO -> {
                    if (checkAdmin(response.user)) {
                        socket.send((Object)
                                new Response(Actions.LOGIN_ADMIN_CORRECTO, User.getAdminFromCadena(getAdmin()), "Inicio de sesion correcto"));
                    } else {
                        socket.send((Object) new Response(Actions.LOGIN_ADMIN_INCORRECTO, "Inicio de session incorrecto"));
                    }
                }
                case LOGIN_CLIENTE_INTENTO -> {

                }
                case USUARIO_ALTA_INTENTO -> {
                    try {
                        añadirCliente(response.user);
                        socket.send((Object) new Response(Actions.USUARIO_ALTA_CORRECTO, "Usuario añadido correctamente"));
                    } catch (IOException ex) {
                        socket.send((Object) new Response(Actions.USUARIO_ALTA_INCORRECTO, "El usuario no se ha podido añadir de forma correcta"));
                    }
                }
                case USUARIO_BAJA_INTENTO -> {

                }
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
    public static boolean checkAdmin(User usr) {
        String cadena = getAdmin();
        User admin = new User();
        admin.userName = cadena.split(":")[1];
        admin.password = cadena.split(":")[2];
        return admin.equals(usr.userName) && BCrypt.checkpw(usr.password, admin.password);
    }
    public static User checkUser(User user) {
        ArrayList<String> users = getUsers();
        int i = 0;
        boolean found = false;
        String usuario = "";
        while ( i < users.size() && found == false) {
            usuario = users.get(i);
            if (usuario.split(":")[2].equals(user.userName)) {
                found = false;
            } else {
                i++;
            }
        }
        User usr;
        if (found) {
            usr = User.getUserFromCadena(usuario);
            if (!BCrypt.checkpw(user.password, usr.password)) {
                usr = null;
            }
        } else {
            usr = null;
        }
        return usr;
    }
    public static String getAdmin() {
        FileManager.Reader reader = FileManager.getReader(FILE_USERS);
        String admin = "";
        try {
            reader.start();
            admin = reader.readLine();
            reader.close();
        } catch (IOException e) {
        }
        return admin;
    }
    public static void añadirCliente(User usr) throws IOException {
        FileManager.Writer writer = FileManager.getWriter(FILE_USERS);
        ArrayList<String> filecontent = getUsers();
        int newID = filecontent.size()+1;
        filecontent.add(newID+":"+true+usr.getCadenaNewClient());
        writer.start();
        writer.writeAll(filecontent);
        writer.close();
    }
    public static ArrayList<String> getUsers() {
        FileManager.Reader reader = FileManager.getReader(FILE_USERS);
        ArrayList<String> filecontent;
        try {
            reader.start();
            filecontent = reader.readAll();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return filecontent;
    }
}
