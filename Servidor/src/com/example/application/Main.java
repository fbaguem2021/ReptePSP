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
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Servidor
 * @author Francesc Bagué Martí
 */
public class Main {
    public static final void separador(){System.out.println("==================================================");}
    public static final String FOLDER_ESPECTACLES =
            "src"+ File.separator+"files"+ File.separator+"performances"+File.separator;
    public static final String FILE_USERS =
            "src"+ File.separator+"files"+ File.separator+"users.txt";
    public static final String FILE_ESPECTACLES =
            "src"+ File.separator+"files"+ File.separator+"espectacles.txt";

    public static void main(String[] args) throws IOException {
        boolean err = false;
        boolean porterr = false;
        while(true) {
            int i = 0;
            MySocket socket = new MySocket(5000);
            if (!err) {
                System.out.println("Esperando conexiones");
            } else {
                System.out.println("recuperando conexión");
                err = false;
            }
            try {
                do {
                    porterr = false;
                    try {
                        socket.accept();
                    } catch (Exception e) {
                        i++;
                        socket = new MySocket(5000+i);
                        porterr = true;
                    }
                } while (porterr);

                System.out.println(socket.getIP());
                System.out.println("conexión iniciada");
                //socket.close();
                lunchThread(socket);
                //lunchNormal(socket);
                System.out.println(" - - - - - ");
            } catch (Exception ex) {
                System.out.println("Ha ocurrido un error");
                socket.close();
                err = true;
                ex.printStackTrace();
            }
        }
    }
    public static void lunchNormal(MySocket socket) throws SocketException {
        try {
            socketMenu(socket);
            socket.close();
        } catch (SocketException exception) {
            System.out.println("Ha ocurrido un error de conexión");
            throw exception;
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    // metode que llença el thread amb una nova conexió
    public static void lunchThread(MySocket socket) {
        try {
            new MyThread().startThread(new MyThread.setOnThreadRun() {
                @Override
                public void onThread() {
                    try {
                        socketMenu(socket);
                        socket.close();
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception ex) {
            System.out.println("ha ocurrido un error de conexión");
            throw ex;
        }
    }
    // Metode que funciona de menu y que mitgançant un switch i una classe enum,
    // detecta quina es l'accio que realitza l'usuari
    public static void socketMenu(MySocket socket) throws IOException, ClassNotFoundException {
        boolean sortir = false;
        do {
            separador();
            User user, usr, usuario;
            Response res;
            String espectacle, espectaculo;
            Entrada entrada;
            ArrayList<String> sillas;
            Response response = (Response) socket.readObject();
            switch (response.action) {
                // administrador intentando hacer login
                case LOGIN_ADMIN_INTENTO:
                    System.out.println("LOGIN_ADMIN_INTENTO");
                    if (checkAdmin(response.user)) {
                        socket.send((Object)
                                new Response(LOGIN_ADMIN_CORRECTO, User.getAdminFromCadena(getAdmin()), "Inicio de sesion correcto"));
                    } else {
                        socket.send((Object) new Response(LOGIN_ADMIN_INCORRECTO, "Inicio de session incorrecto"));
                    }
                    break;
                //clioente intentando hacer login
                case LOGIN_CLIENTE_INTENTO:
                    System.out.println("LOGIN_CLIENTE_INTENTO");
                    usr = checkUser(response.user);
                    if (usr != null) {
                        if (BCrypt.checkpw(response.user.password, usr.password)) {
                            socket.send((Object) new Response(LOGIN_CLIENTE_CORRECTO, usr));
                        } else {
                            socket.send((Object) new Response(LOGIN_CLIENTE_INCORRECTO, "Contraseña incorrecta"));
                        }
                    } else {
                        socket.send((Object) new Response(LOGIN_CLIENTE_INCORRECTO, "No se ha encontrado el usuario"));
                    }
                break;
                // intento de alta de un usuario
                case USUARIO_ALTA_INTENTO:
                    System.out.println("USUARIO_ALTA_INTENTO");
                    try {
                        if (checkNewUser(response.user)) {
                            socket.send((Object) new Response(USUARIO_ALTA_INCORRECTO, "Ya existe el nombre de usuario"));
                        } else {
                            añadirCliente(response.user);
                            socket.send((Object) new Response(USUARIO_ALTA_CORRECTO, "Usuario añadido correctamente"));
                        }
                    } catch (IOException ex) {
                        socket.send((Object) new Response(USUARIO_ALTA_INCORRECTO, "El usuario no se ha podido añadir de forma correcta"));
                    }
                break;
                // intento de baja de un usuario
                case USUARIO_BAJA_INTENTO:
                    System.out.println("USUARIO_BAJA_INTENTO");
                    usuario = response.user;
                    if (desactivarUsuario(usuario)) {
                        socket.send((Object) new Response(USUARIO_BAJA_CORRECTO, "La baja se ha realizado correctamente"));
                    } else {
                        socket.send((Object) new Response(USUARIO_BAJA_INCORRECTO, "La baja no se ha realizado correctyamente"));
                    }
                break;
                // intento de modificación de un usuario
                case USUARIO_MODIFICAR_INTENTO:
                    System.out.println("USUARIO_MODIFICAR_INTENTO");
                    if (modificarUsuario(response.user)) {
                        socket.send((Object) new Response(USUARIO_MODIFICAR_CORRECTO, response.user));
                    } else {
                        socket.send((Object) new Response(USUARIO_MODIFICAR_INCORRECTO, "Ha ocurrido un erroer al intentar crear un usuario\n Vuelve a intentar"));
                    }
                break;
                // intento de obtencion de espectaculos
                case ESPECTACULOS_OBTENER:
                    System.out.println("ESPECTACULOS_OBTENER");
                    res = new Response();
                    res.espectaculos = getEspectaculos();
                    if (res.espectaculos != null) {
                        res.action = ESPECTACULOS_OBTENER_CORRECTO;
                        socket.send((Object) res);
                    } else {
                        res.action = ESPECTACULOS_OBTENER_ERROR;
                        res.message = "Ha ocurrido un error al buscar los espectaculos\n Vuelve a intentar";
                        socket.send((Object) res);
                    }
                break;
                // intento de creacion de espectaculo
                case ESPECTACULO_CREAR_INTENTO:
                    System.out.println("ESPECTACULO_CREAR_INTENTO");
                    espectacle = response.espectaculo;
                    if (!checkEspectaculo(espectacle)) {
                        res = new Response();
                        res.action = ESPECTACULO_CREAR_CORRECTO;
                        crearEspectaculo(espectacle);
                        socket.send((Object) res);
                        System.out.println(1);
                    } else {
                        res = new Response();
                        res.action = ESPECTACULO_CREAR_INCORRECTO;
                        res.message = "El espectaculo ya existe";
                        socket.send((Object)res);
                        System.out.println(2);
                    }
                break;
                // intento de ver las entradas que un usuario puede reservar
                case ENTRADAS_RESERVAR_MOSTRAR_DISPONIBLES:
                    System.out.println("ENTRADAS_RESERVAR_MOSTRAR_DISPONIBLES");
                    usuario = response.user;
                    espectaculo = response.espectaculo;
                    sillas = getSillasNoDisponibles(usuario, espectaculo);
                    if (sillas.size() == 0) {
                        res = new Response();
                        res.action = ENTRADAS_RESERVAR_MOSTRAR_DISPONIBLES;
                        res.message = "Todas las sillas estan disponibles";
                        res.sillas = sillas;
                        socket.send((Object) res);
                    } else {
                        res = new Response();
                        res.action = ENTRADAS_RESERVAR_CORRECTO;
                        res.sillas = sillas;
                        socket.send((Object) res);
                    }
                break;
                // intento de un usuario de reservar entradas
                case ENTRADAS_RESERVAR_INTENTO:
                    System.out.println("ENTRADAS_RESERVAR_INTENTO");
                    user = response.user;
                    entrada = response.entrada;
                    reservarEntradas(user, entrada);
                    res = new Response();
                    res.action = ENTRADAS_RESERVAR_CORRECTO;
                    res.message = "La entrada se ha reservado correctamente";
                    socket.send((Object) res);
                break;
                // intento de ver las entradas que un usuario puede anular
                case ENTRADAS_ANULAR_MOSTRAR_DISPONIBLES:
                    System.out.println("ENTRADAS_ANULAR_MOSTRAR_DISPONIBLES");
                    user = response.user;
                    espectaculo = response.espectaculo;
                    sillas = obtenerReservadas(user, espectaculo);
                    if (sillas.size() == 0) {
                        res = new Response();
                        res.action = ENTRADAS_RESERVAR_MOSTRAR_DISPONIBLES;
                        res.message = "No se han encontrado entradas a tu nombre";
                        socket.send((Object) res);
                    } else {
                        res = new Response();
                        res.action = ENTRADAS_RESERVAR_MOSTRAR_DISPONIBLES;
                        res.sillas = sillas;
                        socket.send((Object) res);
                    }
                break;
                // entento de anular una entrada
                case ENTRADAS_ANULAR_INTENTO:
                    System.out.println("ENTRADAS_ANULAR_INTENTO");
                    user = response.user;
                    entrada = response.entrada;
                    anularEntrada(user, entrada);

                    res = new Response();
                    res.action = ENTRADAS_ANULAR_CORRECTO;
                    res.message = "Entradas anuladas correctamente";
                    socket.send((Object) res);
                break;
                // accion de cerrar session
                case LOGIN_CERRAR_SESSION:
                    System.out.println("LOGIN_CERRAR_SESSION");
                    socket.send(new Response(LOGIN_CERRAR_SESSION_CORRECTO));
                break;
                case APP_CERRAR:
                    sortir = true;
                break;
            }
        } while (!sortir);
    }
    // Comprova si l'usuari que s'esta intentant crear existeix
    private static boolean checkNewUser(User user) {
        ArrayList<String> users = getUsers();
        int i = 1;
        boolean alreadyExists = false;
        while (i < users.size() && !alreadyExists) {
            if ( User.getUserFromCadena(users.get(i)).userName.equals(user.userName) ) {
                alreadyExists = true;
            }
            i++;
        }
        return alreadyExists;
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
            e.printStackTrace();
        }
        return sillas;
    }

    private static void anularEntrada(User user, Entrada entrada) {
        ArrayList<String> file = getEspectaculoFile(entrada.espectaculo);
        String sillaorigen = entrada.fila+":"+entrada.columna+":C:"+user.userName;
        int i = 0;
        boolean found = false;
        String silla = "";
        String line;
        while ( i < file.size() && !found) {
            line = file.get(i);
            if (line.equals(sillaorigen)) {
                silla = entrada.fila+":"+entrada.columna+":L";
                found = true;
            } else i++;
        }
        file.set(i, silla);
        FileManager.Writer writer = FileManager.getWriter(FOLDER_ESPECTACLES+entrada.espectaculo+".txt");
        try {
            writer.start();
            writer.writeAll(file);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void reservarEntradas(User user, Entrada entrada) {
        ArrayList<String> file = getEspectaculoFile(entrada.espectaculo);
        String sillaorigen = entrada.fila+":"+entrada.columna+":L";
        int i = 0;
        boolean found = false;
        String silla = "";
        String line;
        while ( i < file.size() && !found) {
            line = file.get(i);
            if (line.equals(sillaorigen)) {
                silla = entrada.fila+":"+entrada.columna+":C:"+user.userName;
                found = true;
            } else i++;
        }
        file.set(i, silla);
        FileManager.Writer writer = FileManager.getWriter(FOLDER_ESPECTACLES+entrada.espectaculo+".txt");
        try {
            writer.start();
            writer.writeAll(file);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> getSillasNoDisponibles(User usuario, String espectaculo) throws IOException {
        ArrayList<String> sillas = new ArrayList<>();
        FileManager.Reader reader = FileManager.getReader(FOLDER_ESPECTACLES+espectaculo+".txt");
        String line;
        reader.start();
        line = reader.readLine(); line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            if (!line.contains(":L") && line.contains(usuario.userName)) {
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
            e.printStackTrace();
        }
        return file;
    }
    // COmprova que les dades intrduides coincideixin amb les del administrador
    public static boolean checkAdmin(User usr) {
        String cadena = getAdmin();
        String[] data = cadena.split(":");
        User admin = new User();
        admin.userName = data[1];
        admin.password = data[2];
        return admin.userName.equals(usr.userName) && BCrypt.checkpw(usr.password, admin.password);
    }
    // Comprova si hi ha un usuario amb el nom d'usuari introduit
    public static User checkUser(User user) {
        ArrayList<String> usuarios;
        usuarios = getUsers();
        int i = 1;
        boolean found = false;
        String s = "";
        while ( i < usuarios.size() && !found) {
            s = usuarios.get(i);
            if (s.contains(user.userName)) {
                found = true;
            }
            i++;
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
            e.printStackTrace();
        }
        return espectaculos;
    }
    // Metode que afegeix un usuari al archiu
    public static void añadirCliente(User usr) throws IOException {
        FileManager.Writer writer = FileManager.getWriter(FILE_USERS);
        ArrayList<String> filecontent = getUsers();
        int newID = filecontent.size();
        filecontent.add(usr.getCadenaNewClient());
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }
    // Crea el fichero para el nuevo espectaculo
    public static void crearFicheroEspectaculo(String espectaculo) {
        FileManager.Writer writer = FileManager.getWriter(FOLDER_ESPECTACLES+espectaculo+".txt");
        try {
            writer.start();
            writer.writeLine(espectaculo+"\n");
            for (int i = 1; i <= 10; i++) {
                for (int j = 1; j <= 10; j++) {
                    writer.writeLine("\n"+i+":"+j+":"+"L");
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
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
