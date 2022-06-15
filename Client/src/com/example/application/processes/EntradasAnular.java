package com.example.application.processes;

import com.example.application.classes.MySocket;
import com.example.application.classes.ReadM;
import com.example.application.models.Entrada;
import com.example.application.models.Response;
import com.example.application.models.User;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.application.models.Actions.*;

public class EntradasAnular {
    protected static void anular(MySocket socket, User user, String espectaculo) {
        try {
            Response res = new Response(ENTRADAS_ANULAR_MOSTRAR_DISPONIBLES);
            res.user = user;
            res.espectaculo = espectaculo;
            socket.send((Response) res);
            Response respuesta = (Response) socket.readObject();

            if (respuesta.sillas.size() == 0) {
                System.out.println("No has reservado ninguna entrada para este espectaculo");
            } else {
                int[] silla = escogerSilla(respuesta.sillas);
                if (comprovarSilla(user, respuesta.sillas, silla)) {
                    res = null;
                    res = new Response(ENTRADAS_ANULAR_INTENTO);
                    res.user = user;
                    res.entrada = new Entrada(espectaculo, silla[0], silla[1]);
                    socket.send((Response) res);

                    respuesta = null;
                    respuesta = (Response) socket.readObject();
                    if (respuesta.action == ENTRADAS_ANULAR_CORRECTO) {
                        System.out.println(respuesta.message);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static boolean comprovarSilla(User user, ArrayList<String> sillas, int[] silla) {
        boolean correcto = false;
        int i = 0;

        while ( i < sillas.size() && !correcto) {
            if (sillas.get(i).contains(silla[0]+":"+silla[1]) && sillas.get(i).contains(user.userName)) {
                correcto = true;
            }
            i++;
        }
        return correcto;
    }

    private static int[] escogerSilla(ArrayList<String> sillas) {
        for (int i = 0; i < sillas.size(); i++)  {
            System.out.println(i+"- "+sillas.get(i));
        }
        int[] silla= new int[2];
        silla[0] = ReadM._int("Fila (1 a 10): ");
        while (silla[0] < 0 || silla[0] > 10) {
            silla[0] = ReadM._int(" --> ");
        }

        silla[1] = ReadM._int("Columna (1 a 10): ");
        while (silla[1] < 0 || silla[1] > 10) {
            silla[1] = ReadM._int(" --> ");
        }
        return silla;
    }
}
