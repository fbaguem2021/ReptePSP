package com.example.application.classes;

import com.example.application.classes.Cripto;
import com.example.application.classes.BCrypt;

public class User {
    String  userName;
    String  name;
    String  surname;
    String  email;
    String  password;
    String  phone;
    String  tipoTarjeta;
    String  tarjetaCredito;

    public User() { }
    public User(String userName, String password){
        this.userName = userName;
        this.password = password;
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
    public String toString() {
        return "Usuario: \nNombre de usuario: " + userName + ", Nombre: " + name
                + ", Apellidos: " + surname + ", Email: " + email
                + ", Contraseña: " + password + ", Numero de Telefono: " + phone
                + ", Tipo de tarjeta: " + tipoTarjeta + ", Numero de tarjeta: " + tarjetaCredito;
    }
}
