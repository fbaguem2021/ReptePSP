package com.example.application;

import com.example.application.classes.*;
import com.example.application.classes.MyThread.setOnThreadRunArgs;
import com.example.application.models.Actions;
import com.example.application.models.Response;
import com.example.application.models.User;
import com.sun.security.jgss.GSSUtil;
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
                    User usr = checkUser(response.user);
                    if (usr != null) {
                        if (BCrypt.checkpw(response.user.password, usr.password)) {
                            socket.send((Object) new Response(Actions.LOGIN_CLIENTE_CORRECTO, usr));
                        } else {
                            socket.send((Object) new Response(Actions.LOGIN_CLIENTE_INCORRECTO, "Contraseña incorrecta"));
                        }
                    } else {
                        socket.send((Object) new Response(Actions.LOGIN_CLIENTE_INCORRECTO, "No se ha encontrado el usuario"));
                    }
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
                    User usuario = response.user;
                    if (desactivarUsuario(usuario)) {
                        socket.send((Object) new Response(Actions.USUARIO_BAJA_CORRECTO, "La baja se ha realizado correctamente"));
                    } else {
                        socket.send((Object) new Response(Actions.USUARIO_BAJA_INCORRECTO, "La baja no se ha realizado correctyamente"));
                    }
                }
                case USUARIO_MODIFICAR_INTENTO -> {
                    if (modificarUsuario(response.user)) {
                        socket.send((Object) new Response(Actions.USUARIO_MODIFICAR_CORRECTO, response.user));
                    } else {
                        socket.send((Object) new Response(Actions.USUARIO_MODIFICAR_INCORRECTO, "Ha ocurrido un erroer al intentar crear un usuario\n Vuelve a intentar"));
                    }
                }
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
        ArrayList<String> usuarios;
        usuarios = getUsers();
        int i = 0;
        boolean found = false;
        String s = "";
        while ( i < usuarios.size() && !found) {
            s = usuarios.get(i);
            if (s.contains(user.name)) {
                found = true;
            }
        }
        User usr = null;
        if (found) {
            usr = User.getUserFromCadena(s);
        }
        return usr;
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
    public static boolean modificarUsuario(User usr) {
        boolean correct = true;
        try {
            ArrayList<String> usuarios = getUsers();
            String cadena = usr.getCadenaModClient();
            usuarios.set(usr.id, cadena);
            reescrivirUsuarios(usuarios);
        } catch (Exception ex) {
            correct = false;
        }
        return correct;
    }
    public static boolean desactivarUsuario(User usr) {
        ArrayList<String> usuarios = getUsers();
        int i = 0;
        int id = 0;
        String s = "";
        boolean found = false;
        while (i < usuarios.size() && !found) {
            s = usuarios.get(i);
            if (s.startsWith(usr.id+":")) {
                id = i;
                found = true;
            } else {
                i++;
            }
        }

        if (found) {
            String[] vals = s.split(":");
            vals[1] = "false";
            s = regenerarCadena(vals);
            usuarios.set(id, s);
            reescrivirUsuarios(usuarios);
        }
        return found;
    }
    public static String getAdmin() {
        FileManager.Reader reader = FileManager.getReader(FILE_USERS);
        String admin = "";
        try {
            reader.start();
            admin = reader.readLine();
            reader.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return admin;
    }
    public static ArrayList<String> getUsers() {
        FileManager.Reader reader = FileManager.getReader(FILE_USERS);
        ArrayList<String> filecontent = new ArrayList<>();
        try {
            reader.start();
            filecontent = reader.readAll();
            reader.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return filecontent;
    }
    // Reescrive el archivo para actualizar los usuarios
    public static void reescrivirUsuarios(ArrayList<String> usuarios) {
        FileManager.Writer writer = FileManager.getWriter(FILE_USERS);
        try {
            writer.start();
            writer.writeAll(usuarios);
            writer.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
    // Agafa els valors del array i els torna a juntar en una cadena
    public static String regenerarCadena(String[] vals) {
        return  vals[0]+":"+vals[1]+":"+
                vals[2]+":"+vals[3]+":"+
                vals[4]+":"+vals[5]+":"+
                vals[6]+":"+vals[7]+":"+
                vals[8]+":"+vals[9];
    }
}
