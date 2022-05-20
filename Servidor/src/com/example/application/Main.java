package com.example.application;

import com.example.application.classes.*;
import static com.example.application.models.Actions.*;

import com.example.application.models.Entrada;
import com.example.application.models.Response;
import com.example.application.models.User;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author francesc
 */
public class Main {
    public static final String FOLDER_ESPECTACLES =
            "src\\files"+ File.separator+"performances"+File.separator;
    public static final String FILE_USERS =
            "src\\files"+ File.separator+"users.txt";
    public static final String FILE_ESPECTACLES =
            "src\\files"+ File.separator+"espectacles.txt";

    public static void main(String[] args) {
        int i = 0;
        while(true) {
            final MySocket socket = new MySocket(5000+i);
            System.out.println("Esperando conexiones");
            try {
                socket.accept();
                System.out.println(socket.getIP());
                System.out.println("conexión iniciada");
                lunchThread(socket);
                System.out.println(" - - - - - ");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            i++;
        }
    }
    // metode que llença el thread amb una nova conexió
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
                // administrador intentando hacer login
                case LOGIN_ADMIN_INTENTO -> {
                    if (checkAdmin(response.user)) {
                        socket.send((Object)
                                new Response(LOGIN_ADMIN_CORRECTO, User.getAdminFromCadena(getAdmin()), "Inicio de sesion correcto"));
                    } else {
                        socket.send((Object) new Response(LOGIN_ADMIN_INCORRECTO, "Inicio de session incorrecto"));
                    }
                }
                //clioente intentando hacer login
                case LOGIN_CLIENTE_INTENTO -> {
                    User usr = checkUser(response.user);
                    if (usr != null) {
                        if (BCrypt.checkpw(response.user.password, usr.password)) {
                            socket.send((Object) new Response(LOGIN_CLIENTE_CORRECTO, usr));
                        } else {
                            socket.send((Object) new Response(LOGIN_CLIENTE_INCORRECTO, "Contraseña incorrecta"));
                        }
                    } else {
                        socket.send((Object) new Response(LOGIN_CLIENTE_INCORRECTO, "No se ha encontrado el usuario"));
                    }
                }
                // intento de alta de un usuario
                case USUARIO_ALTA_INTENTO -> {
                    try {
                        añadirCliente(response.user);
                        socket.send((Object) new Response(USUARIO_ALTA_CORRECTO, "Usuario añadido correctamente"));
                    } catch (IOException ex) {
                        socket.send((Object) new Response(USUARIO_ALTA_INCORRECTO, "El usuario no se ha podido añadir de forma correcta"));
                    }
                }
                // intento de baja de un usuario
                case USUARIO_BAJA_INTENTO -> {
                    User usuario = response.user;
                    if (desactivarUsuario(usuario)) {
                        socket.send((Object) new Response(USUARIO_BAJA_CORRECTO, "La baja se ha realizado correctamente"));
                    } else {
                        socket.send((Object) new Response(USUARIO_BAJA_INCORRECTO, "La baja no se ha realizado correctyamente"));
                    }
                }
                // intento de modificación de un usuario
                case USUARIO_MODIFICAR_INTENTO -> {
                    if (modificarUsuario(response.user)) {
                        socket.send((Object) new Response(USUARIO_MODIFICAR_CORRECTO, response.user));
                    } else {
                        socket.send((Object) new Response(USUARIO_MODIFICAR_INCORRECTO, "Ha ocurrido un erroer al intentar crear un usuario\n Vuelve a intentar"));
                    }
                }
                // intento de obtencion de espectaculos
                case ESPECTACULOS_OBTENER -> {
                    Response res = new Response();
                    res.espectaculos = getEspectaculos();
                    if (res.espectaculos != null) {
                        res.action = ESPECTACULOS_OBTENER_CORRECTO;
                        socket.send((Object) res);
                    } else {
                        res.action = ESPECTACULOS_OBTENER_ERROR;
                        res.message = "Ha ocurrido un error al buscar los espectaculos\n Vuelve a intentar";
                        socket.send((Object) res);
                    }
                }
                // intento de creacion de espectaculo
                case ESPECTACULO_CREAR_INTENTO -> {
                    String espectacle = response.espectaculo;
                    if (checkEspectaculo(espectacle)) {
                        Response res = new Response();
                        res.action = ESPECTACULO_CREAR_CORRECTO;
                        crearEspectaculo(espectacle);
                        socket.send((Object) res);
                    } else {
                        Response res = new Response();
                        res.action = ESPECTACULO_CREAR_INCORRECTO;
                        res.message = "El espectaculo ya existe";
                        socket.send((Object)res);
                    }
                }
                // intento de ver las entradas que un usuario puede reservar
                case ENTRADAS_RESERVAR_MOSTRAR_DISPONIBLES -> {
                    User usuario = response.user;
                    String espectaculo = response.espectaculo;
                    ArrayList<String> sillas = getSillasNoDisponibles(usuario, espectaculo);
                    if (sillas.size() == 0) {
                        Response res = new Response();
                        res.action = ENTRADAS_RESERVAR_MOSTRAR_DISPONIBLES;
                        res.message = "Todas las sillas estan disponibles";
                        socket.send((Object) res);
                    } else {
                        Response res = new Response();
                        res.action = ENTRADAS_RESERVAR_CORRECTO;
                        res.sillas = sillas;
                    }
                }
                // intento de un usuario de reservar entradas
                case ENTRADAS_RESERVAR_INTENTO -> {
                    User user = response.user;
                    Entrada entrada = response.entrada;
                    reservarEntradas(user, entrada);
                    Response res = new Response();
                    res.action = ENTRADAS_RESERVAR_CORRECTO;
                    res.message = "La entrada se ha reservado correctamente";
                    socket.send((Object) res);
                }
                // intento de ver las entradas que un usuario puede anular
                case ENTRADAS_ANULAR_MOSTRAR_DISPONIBLES -> {
                    User user = response.user;
                    String espectaculo = response.espectaculo;
                    ArrayList<String> sillas = obtenerReservadas(user, espectaculo);
                    if (sillas.size() == 0) {
                        Response res = new Response();
                        res.action = ENTRADAS_RESERVAR_MOSTRAR_DISPONIBLES;
                        res.message = "No se han encontrado entradas a tu nombre";
                        socket.send((Object) res);
                    } else {
                        Response res = new Response();
                        res.action = ENTRADAS_RESERVAR_MOSTRAR_DISPONIBLES;
                        res.sillas = sillas;
                        socket.send((Object) res);
                    }
                }
                // entento de anular una entrada
                case ENTRADAS_ANULAR_INTENTO -> {
                    User user = response.user;
                    Entrada entrada = response.entrada;
                    anularEntrada(user, entrada);

                    Response res = new Response();
                    res.action = ENTRADAS_ANULAR_CORRECTO;
                    res.message = "Entradas anuladas correctamente";
                    socket.send((Object) res);
                }
                // accion de cerrar session
                case LOGIN_CERRAR_SESSION -> {
                    socket.send(new Response(LOGIN_CERRAR_SESSION_CORRECTO));
                    sortir = true;
                }
            }
        } while (sortir);
    }

    private static ArrayList<String> obtenerReservadas(User user, String espectaculo) {
        ArrayList<String> sillas = new ArrayList<>();
        FileManager.Reader reader = FileManager.getReader(FOLDER_ESPECTACLES+espectaculo+".txt");
        String line;
        try {
            reader.start();
            while ((line = reader.readLine()) != null) {
                if (line.contains(user.userName)) {
                    sillas.add(line);
                }
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sillas;
    }

    private static void anularEntrada(User user, Entrada entrada) {
        ArrayList<String> file = getEspectaculoFile(entrada.espectaculo);

        for (int i = 2; i < file.size(); i++) {
            String silla = file.get(i);
            if (silla.contains(entrada.fila+":"+entrada.columna) && silla.contains(user.name)) {
                silla = entrada.fila+":"+entrada.columna+":L";
            }
        }

        FileManager.Writer writer = FileManager.getWriter(FOLDER_ESPECTACLES+entrada.espectaculo+".txt");
        try {
            writer.start();
            writer.writeAll(file);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void reservarEntradas(User user, Entrada entrada) {
        ArrayList<String> file = getEspectaculoFile(entrada.espectaculo);

        for (int i = 2; i < file.size(); i++) {
            String silla = file.get(i);
            if (silla.contains(entrada.fila+":"+entrada.columna) && silla.contains(":L")) {
                silla = entrada.fila+":"+entrada.columna+":C"+user.userName;
            }
        }

        FileManager.Writer writer = FileManager.getWriter(FOLDER_ESPECTACLES+entrada.espectaculo+".txt");
        try {
            writer.start();
            writer.writeAll(file);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ArrayList<String> getSillasNoDisponibles(User usuario, String espectaculo) throws IOException {
        ArrayList<String> sillas = new ArrayList<>();
        FileManager.Reader reader = FileManager.getReader(FOLDER_ESPECTACLES+espectaculo+".txt");
        String line;
        reader.start();
        line = reader.readLine(); line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            if (!line.contains(":L") && !line.contains(usuario.userName)) {
                sillas.add(line);
            }
        }
        return sillas;
    }
    private static ArrayList<String> getEspectaculoFile(String espectaculo) {
        ArrayList<String> file = new ArrayList<>();
        FileManager.Reader reader = FileManager.getReader(FOLDER_ESPECTACLES+espectaculo+".txt");
        try {
            reader.start();
            file = reader.readAll();
            reader.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return file;
    }
    // COmprova que les dades intrduides coincideixin amb les del administrador
    public static boolean checkAdmin(User usr) {
        String cadena = getAdmin();
        User admin = new User();
        admin.userName = cadena.split(":")[1];
        admin.password = cadena.split(":")[2];
        return admin.equals(usr.userName) && BCrypt.checkpw(usr.password, admin.password);
    }
    // Comprova si hi ha un usuario amb el nom d'usuari introduit
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
    public static boolean checkEspectaculo(String espectaculo) {
        boolean exists = false;
        for (String s : getEspectaculos()) {
            if (s.contains(espectaculo)) {
                exists = true;
            }
        }
        return exists;
    }
    // Metode per a obtenir tots els espectacles
    public static ArrayList<String> getEspectaculos() {
        ArrayList<String> espectaculos = null;
        try {
            FileManager.Reader reader = FileManager.getReader(FILE_ESPECTACLES);
            reader.start();
            espectaculos = reader.readAll();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return espectaculos;
    }
    // Metode que afegeix un usuari al archiu
    public static void añadirCliente(User usr) throws IOException {
        FileManager.Writer writer = FileManager.getWriter(FILE_USERS);
        ArrayList<String> filecontent = getUsers();
        int newID = filecontent.size()+1;
        filecontent.add(newID+":"+true+usr.getCadenaNewClient());
        writer.start();
        writer.writeAll(filecontent);
        writer.close();
    }
    // metode que modifica un usuari
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
    // metode que desactiva un usuari
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
    // metode que retorna la cadena encriptada del administrador
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
    // metode que retorna un arraylist amb tots els usuaris
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
    public static void crearEspectaculo(String espectaculo) {
        ArrayList<String> espectaculos = getEspectaculos();
        espectaculos.add(espectaculo);
        reescrivirEspectaculos(espectaculos);
        crearFicheroEspectaculo(espectaculo);
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
    // Añade el nuevo espectaculo al fichero de espectaculos
    public static void reescrivirEspectaculos(ArrayList<String> espectaculos) {
        FileManager.Writer writer = FileManager.getWriter(FILE_ESPECTACLES);
        try {
            writer.start();
            writer.writeAll(espectaculos);
            writer.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
    // Crea el fichero para el nuevo espectaculo
    public static void crearFicheroEspectaculo(String espectaculo) {
        FileManager.Writer writer = FileManager.getWriter(FOLDER_ESPECTACLES+espectaculo+".txt");
        try {
            writer.start();
            writer.writeLine(espectaculo);
            for (int i = 1; i <= 10; i++) {
                for (int j = 1; j <= 10; j++) {
                    writer.writeLine(i+":"+j+":"+"L");
                }
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
