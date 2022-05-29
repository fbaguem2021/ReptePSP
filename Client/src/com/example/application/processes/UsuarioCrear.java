package com.example.application.processes;

import com.example.application.classes.ReadM;
import com.example.application.models.User;

import java.util.Objects;

/**
 * Classe con los metodos para la creación de un usuario
 * @author Francesc Baguñé Martí
 */
public class UsuarioCrear {
    // Metodo para crear un usuario
    public static User crear() {
        User user = new User();
        user.name = nombre();
        user.surname = apellidos();
        user.userName = userName();
        user.email = email();
        user.password = password();
        user.phone = phone();
        user.tarjetaCredito = tarjeta();

        return user;
    }

    // metodo para introducir un nombre
    private static String nombre()      {
        String name = ReadM._String("Nombre: ");

        while (name.equals("")) {
            System.out.println("Deves introducir un nombre");
            name = ReadM._String(" --> ");
        }
        return name;
    }
    // metodo para introducir los apellidos
    private static String apellidos()   {
        String s = ReadM._String("Apellido/s: ");

        while (s.equals("")) {
            System.out.println("Deves introducir minimo un apellido");
            s = ReadM._String("  --> ");
        }
        return s;
    }
    // metodo para introducir el nombre de usuario
    private static String userName()    {
        String s = ReadM._String("Nombr de usuairo: ");

        while (s.equals("")) {
            System.out.println("Deves introducir un nombre de usuario");
            s = ReadM._String("  --> ");
        }
        return s;
    }
    // metodo para introducir el email
    private static String email()       {
        String s = ReadM._String("Email: ");

        while (s.equals("")) {
            System.out.println("Deves introducir un correo");
            s = ReadM._String(" --> ");
        }
        return s;
    }
    // metodo para introducir la contraseña
    private static String password()    {
        String s = ReadM._String("Contraseña (min 8 letras): ");

        while (Objects.equals(s, "") || s.length() < 8) {
            System.out.println("Deves introducir un ");
            s = ReadM._String(" --> ");
        }
        return s;
    }
    // metodo para introducir el numero de telefono
    private static String phone() {
        String s = ReadM._String("Numero de telefono: ");
        // Comprova si el valor es numeric, si no ho es el valor es posa
        // com vuit per a que entri al bucle
        try { Integer.parseInt(s); } catch (Exception e) { s=""; }

        while (s.equals("")) {
            System.out.println("Deves introducir un numero de telefono");
            s = ReadM._String(" --> ");
            // Comprova si el valor es numeric, si no ho es el valor es posa
            // com vuit per a que entri al bucle
            if (!s.equals("")) {
                try { Integer.parseInt(s); } catch (Exception e) { s=""; }
            }
        }
        return s;
    }
    // metodo para introducir la tarjeta de credito
    private static String tarjeta()     {
        String s = ReadM._String("Tarjeta de credito");

        while (s.equals("")) {
            System.out.println("Deves introducir una tarjeta de crediito");
            s = ReadM._String(" -->> ");
        }
        return s;
    }
}
