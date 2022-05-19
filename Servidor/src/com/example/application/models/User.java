package com.example.application.models;

import com.example.application.classes.Cripto;
import com.example.application.classes.BCrypt;
import static com.example.application.classes.Cripto.AES_hash;
import static com.example.application.classes.Cripto.AES_unhash;

public class User {
    public int id;
    public boolean active;
    public String  userName;
    public String  name;
    public String  surname;
    public String  email;
    public String  password;
    public String  phone;
    public String  tipoTarjeta;
    public String  tarjetaCredito;

    public User() { }
    public User(String userName, String password){
        this.userName = userName;
        this.password = password;
    }
    public static User getAdminFromCadena(String cadena) {
        User admin = new User();
        admin.userName = cadena.split(":")[0];
        admin.password = cadena.split(":")[1];
        return admin;
    }
    public static User getUserFromCadena(String cadena) {
        User user = new User();
        int i = -1;
        user.id = Integer.parseInt(cadena.split(":")[i++]);
        user.active = Boolean.getBoolean(cadena.split(":")[i++]);
        user.userName = cadena.split(":")[i++];
        user.name = cadena.split(":")[i++];
        user.surname = cadena.split(":")[i++];
        user.email = cadena.split(":")[i++];
        user.password = cadena.split(":")[i++];
        user.phone = cadena.split(":")[i++];
        user.tipoTarjeta = cadena.split(":")[i++];
        user.tarjetaCredito = cadena.split(":")[i++];

        return user;
    }
    private User(String usuario, String text, boolean admin){
        this.userName = usuario;
        if (admin == true){
            this.password = text;
        }else if (admin == false){
            this.email = text;
        }
    }
    public User(String userName, String name, String surname, String email, String password,
                String phone, String tipoTarjeta, String tarjetaCredito/*, boolean create*/) {
        this.userName       = userName;
        this.name           = name;
        this.surname        = surname;
        this.email          = email;
        this.password       = password;
        this.phone          = phone;
        this.tipoTarjeta    = tipoTarjeta;
        this.tarjetaCredito = tarjetaCredito;
    }
    public String getCadena() {
        return userName+':'+password;
    }
    public String getCadenaNewClient() {
        return id+":"+active+":"+userName+":"+name+":"+surname+":"+email+":"+password+":"+phone+":"+tipoTarjeta+":"+tarjetaCredito;
    }
    public String getCadenaModClient() {
        return id+":"+active+":"+userName+":"+name+":"+surname+":"+email+":"+password+":"+phone+":"+tipoTarjeta+":"+tarjetaCredito;
    }
    public String toString() {
        return "Usuario: \nNombre de usuario: " + userName + ", Nombre: " + name
                + ", Apellidos: " + surname + ", Email: " + email
                + ", Contraseña: " + password + ", Numero de Telefono: " + phone
                + ", Tipo de tarjeta: " + tipoTarjeta + ", Numero de tarjeta: " + tarjetaCredito;
    }
}
