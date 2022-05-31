package com.example.application.models;

import java.io.Serializable;

public class Entrada implements Serializable {
    public String espectaculo;
    public int fila;
    public int columna;

    public Entrada(){}

    public Entrada(String espectaculo, int fila, int columna) {
        this.espectaculo = espectaculo;
        this.fila = fila;
        this.columna = columna;
    }
}
