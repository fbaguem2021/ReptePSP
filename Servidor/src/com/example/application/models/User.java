package com.example.application.models;

import com.example.application.classes.Cripto;
import com.example.application.classes.BCrypt;

import java.io.Serializable;

import static com.example.application.classes.Cripto.AES_hash;
import static com.example.application.classes.Cripto.AES_unhash;

public class User implements Serializable {
    public int id;
    public boolean active;
    public String  name;
    public String  surname;
    public String  userName;
    public String  email;
    public String  password;
    public String  phone;
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
        user.id = Integer.parseInt(cadena.split(":")[0]);
        user.active = Boolean.getBoolean(cadena.split(":")[1]);
        user.userName = cadena.split(":")[2];
        user.name = cadena.split(":")[3];
        user.surname = cadena.split(":")[4];
        user.email = cadena.split(":")[5];
        user.password = cadena.split(":")[6];
        user.phone = cadena.split(":")[7];
        user.tarjetaCredito = cadena.split(":")[8];

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
                String phone, String tarjetaCredito/*, boolean create*/) {
        this.userName       = userName;
        this.name           = name;
        this.surname        = surname;
        this.email          = email;
        this.password       = password;
        this.phone          = phone;
        this.tarjetaCredito = tarjetaCredito;
    }
    public String getCadena() {
        return userName+':'+password;
    }
    public String getCadenaNewClient() {
        return id+":"+active+":"+userName+":"+name+":"+surname+":"+email+":"+password+":"+phone+":"+tarjetaCredito;
    }
    public String getCadenaModClient() {
        return id+":"+active+":"+userName+":"+name+":"+surname+":"+email+":"+password+":"+phone+":"+tarjetaCredito;
    }
    public String toString() {
        return "Usuario: \nNombre de usuario: " + userName + ", Nombre: " + name
                + ", Apellidos: " + surname + ", Email: " + email
                + ", Contraseña: " + password + ", Numero de Telefono: " + phone
                + ", Numero de tarjeta: " + tarjetaCredito;
    }
}
