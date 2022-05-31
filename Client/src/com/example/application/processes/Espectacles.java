package com.example.application.processes;

import com.example.application.classes.*;
import com.example.application.models.*;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.application.models.Actions.*;

public class Espectacles {
    public static final void separador(){System.out.println("==================================================");}
    public static final String MODO_RESERVA = "RESERVA";
    public static final String MODO_ANULACION = "ANULACION";
    // menu per a la r3eserva y anulacio de entrades
    public static void menu(MySocket socket, User user, String mode) {
        try {
            socket.send((Object) new Response(ESPECTACULOS_OBTENER));
            Response res = (Response) socket.readObject();
            String espectaculo = escogerEspectaculo(res.espectaculos);
            switch (mode) {
                case MODO_RESERVA -> {
                    EntradasComprar.comprar(socket, user, espectaculo);
                }
                case MODO_ANULACION -> {
                    EntradasAnular.anular(socket, user, espectaculo);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private static String escogerEspectaculo(ArrayList<String> espectaculos) {
        separador();
        System.out.println("Espectaculos:");
        for (int i = 0; i < espectaculos.size(); i++) System.out.println(i+"- "+espectaculos.get(i));
        int opcion = ReadM._int("Opcion: ");
        while (opcion < 0 || opcion > espectaculos.size()) {
            opcion = ReadM._int(" --> ");
        }
        return espectaculos.get(opcion);
    }
}
