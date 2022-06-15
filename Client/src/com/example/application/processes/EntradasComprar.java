package com.example.application.processes;

import com.example.application.classes.MySocket;
import com.example.application.classes.ReadM;
import com.example.application.models.Entrada;
import com.example.application.models.Response;
import com.example.application.models.User;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.application.models.Actions.*;

public class EntradasComprar {
    public static final void separador(){System.out.println("==================================================");}
    protected static void comprar(MySocket socket, User user, String espectacle){
        try {
            Response res = new Response();
            res.user = user;
            res.action = ENTRADAS_RESERVAR_MOSTRAR_DISPONIBLES;
            res.espectaculo = espectacle;
            socket.send((Object) res);
            separador();
            Response respuesta = (Response) socket.readObject();
            int[] silla = escogerSilla(respuesta.sillas);
            if (comprovarSilla(respuesta.sillas, silla)) {
                res = new Response();
                res.action = ENTRADAS_RESERVAR_INTENTO;
                res.user = user;
                res.entrada = new Entrada(espectacle, silla[0], silla[1]);
                socket.send((Object) res);

                respuesta = null;
                respuesta = (Response) socket.readObject();

                if (respuesta.action == ENTRADAS_RESERVAR_CORRECTO) {
                    System.out.println("Entrada reservada correctamente");
                }
            } else {
                System.out.println("Esta silla ya està ocupada");
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ha ocurrido un error durante el proceso");
			e.printStackTrace();
        }
    }

    private static boolean comprovarSilla(ArrayList<String> sillas, int[] silla) {
        int i = 0;
        boolean correcto = true;
        while (i < sillas.size() && correcto) {
            if (sillas.get(i).contains(silla[0]+":"+silla[1])) {
                correcto = false;
            }
            i++;
        }
        return correcto;
    }

    private static int[] escogerSilla(ArrayList<String> sillas) {
        int[] silla = new int[2];
        if (sillas.size() == 0) {
            System.out.println("Todas las sillas estan disponibles");
        } else {
            System.out.println("Estas sillas estan ocupadas: ");
            sillas.forEach(System.out::println);
        }
        silla[0] = ReadM._int("Fila: (1 a 10): ");
        while (silla[0]< 0 || silla[0] > 10) {
            silla[0] = ReadM._int(" --> ");
        }
        silla[1] = ReadM._int("Columna: (1 a 10): ");
        while (silla[1]< 0 || silla[1] > 10) {
            silla[1] = ReadM._int(" --> ");
        }
        return silla;
    }
}
