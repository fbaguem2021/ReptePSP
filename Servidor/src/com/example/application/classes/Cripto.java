/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.application.classes;
import com.example.application.models.User;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.example.application.models.*;

/**
 * Classe que conte els diferents mètodes per a l'encriptació de dades
 * @author Francesc Bagué Martí
 */
public class Cripto {
    /**
     * String con la llave para el cifrado AES
     */
    public static final String LLAVE_CIFRADO = "llavedecifrado";
    public static final String CLAVE_CIPHER = "AES/ECB/PKCS5Padding";
    /**
     * Metode per a encriptar mitgançant BlowFish
     * @param contraseña String amb la contraseña
     * @return Retorna un String amb la contraseña encriptada
     */
    public static String BlowFish(String contraseña){
        return BCrypt.hashpw(contraseña, BCrypt.gensalt(10));
    }
    /**
     * Metode per a conprovar si la contraseña es la encriptada
     * @param password String amb la contraseña
     * @param hash String amb la contraseña encriptada
     * @return Retorna un boolean que indica si la contraseña coïncideix
     */
    public static boolean BlowFish_check(String password, String hash){
        return BCrypt.checkpw(password, hash) == true;
    }
    /**
     * Metode per a encriptar la dada mitjançant AES
     * @param dato String amb la dada
     * @return Retorna un String amb la dada encriptada
     */
    public static String AES_hash(String dato) {
        String encriptado = "";
        
        SecretKeySpec key;
        try {
            key = crearClave(LLAVE_CIFRADO);
            Cipher cipher = Cipher.getInstance(CLAVE_CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] datosAEncriptar;
            byte[] bytesEncriptados;

            datosAEncriptar     = dato.getBytes("UTF-8");
            bytesEncriptados    = cipher.doFinal(datosAEncriptar);

            encriptado = Base64.getEncoder().encodeToString(bytesEncriptados);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println(ex.toString());
        }
        
            
        return encriptado;
    }
    /**
     * Metode que utilitza AES per a desencriptar la dada
     * @param dato Stringamb la dada encriptada
     * @return Retorna un String amb la dada desencriptada
     */
    public static String AES_unhash(String dato) {
        String desencriptado = null;
        
        SecretKeySpec key;
        try {
            key = crearClave(LLAVE_CIFRADO);
            
            Cipher cipher = Cipher.getInstance(CLAVE_CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] bytesEncriptados = Base64.getDecoder().decode(dato);
            byte[] datoDesencriptado = cipher.doFinal(bytesEncriptados);
            desencriptado = new String(datoDesencriptado);
            
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println(ex.toString());
        }
        
        return desencriptado;
    }
    /**
     * Metode per a obtenir la Key d'AES
     * @param contraseña Cadena amb la contraseña
     * @return Retorna un SecretKeySpec amb la Key
     * 
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException 
     */
    public static SecretKeySpec crearClave(String contraseña) 
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] claveEncriptar;
        MessageDigest sha;
        SecretKeySpec key;
        
        claveEncriptar = contraseña.getBytes("UTF-8");
        sha = MessageDigest.getInstance("SHA-1");

        claveEncriptar = sha.digest(claveEncriptar);
        claveEncriptar = Arrays.copyOf(claveEncriptar, 16);

        key = new SecretKeySpec(claveEncriptar, "AES");
        
        return key;
    }

    /**
     * Metodo que encripta la contraseña y la tarjeta de credito de un usuario
     * @param usr Objeto usuario
     * @return Retorna un objeto Usuario
     */
    public static User encriptar(User usr) {
        usr.password = BlowFish(usr.password);
        usr.tarjetaCredito = AES_hash(usr.tarjetaCredito);
        return usr;
    }
    public static User desencriptar(User usr) {
        usr.tarjetaCredito = AES_unhash(usr.tarjetaCredito);
        return usr;
    }
    /**
     * Metode que encripta l'informació d'un objecte User, i depenent de si 
     * es produeix un error en el process, retornarà l'objecte encriptat o sense encriptar
     * @param usr Parametre objecte User que contè l'informació de l'usuaRi
     * @return Retorna un objecte User
     */
    public static User encriptarUsuario(User usr){
        String[] datos = {
            usr.userName, usr.name, usr.surname, usr.email, usr.phone, usr.tarjetaCredito
        };
        String[] datosEncriptados = new String[datos.length];
        String contraseñaEncriptada;
        User usuario;
        
        for (int i = 0; i < datos.length; i++) {
            datosEncriptados[i] = AES_hash(datos[i]);
        }
        contraseñaEncriptada = BlowFish(usr.password);
            
        if (datosEncriptados != null){
            usuario = new User(
                    datosEncriptados[0], datosEncriptados[1], datosEncriptados[2], 
                    datosEncriptados[3], contraseñaEncriptada,datosEncriptados[4],
                    datosEncriptados[5]
            );
        }else{
            usuario = usr;
        }
        return usuario;
    }
    public static User desencriptarUsuario(User user, String pssw) {
        User usuario;
        String username, name, surname, email, password, phone, tarjetacredito;
        
        username = AES_unhash(user.userName);
        name = AES_unhash(user.name);
        surname = AES_unhash(user.surname);
        email = AES_unhash(user.email);
        password = pssw;
        phone = AES_unhash(user.phone);
        tarjetacredito = AES_unhash(user.tarjetaCredito);

        usuario = new User(username, name, surname, email, password, phone, tarjetacredito);
        return usuario;
    }
/* ================================= CADENAS =================================*/
    /**
     * Metode que genera una cadena amb les dades de l'Administrador
     * @param admin Parametre objecte User amb les dades de l'usuari Arministrador
     * @return Retorna un String
     */
    protected static String GenerarCadenaContraseña(User admin){
        return admin.userName+':'+admin.phone;
    }
    /**
     * Metode que genera una cadena amb les dades de l'usuari encriptades
     * @param usr Paametre objecte User
     * @return Retorna un String
     */
    public static String GenerarCadenaUsuario(User usr){
        String username, name, surname, email, password, phone, tipoTarjeta, tarjetaCredito;
        username        = usr.userName;
        name            = usr.name;
        surname         = usr.surname;
        email           = usr.email;
        password        = usr.password;
        phone           = usr.phone;
        tarjetaCredito  = usr.tarjetaCredito;
        return username+':'+name+':'+surname+':'+email+':'+password+':'+phone+':'+tarjetaCredito;
    }
}
