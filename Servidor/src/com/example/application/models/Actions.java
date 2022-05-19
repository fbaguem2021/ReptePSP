package com.example.application.models;

import java.io.Serializable;

public enum Actions implements Serializable {
    LOGIN_ADMIN_INTENTO,
    LOGIN_ADMIN_CORRECTO,
    LOGIN_ADMIN_INCORRECTO,
    LOGIN_CLIENTE_INTENTO,
    LOGIN_CLIENTE_CORRECTO,
    LOGIN_CLIENTE_INCORRECTO,
    LOGIN_CERRAR_SESSION,
    LOGIN_CERRAR_SESSION_CORRECTO,
    USUARIO_ALTA_INTENTO,
    USUARIO_ALTA_CORRECTO,
    USUARIO_ALTA_INCORRECTO,
    USUARIO_BAJA_INTENTO,
    USUARIO_BAJA_CORRECTO,
    USUARIO_BAJA_INCORRECTO,
    USUARIO_MODIFICAR_INTENTO,
    USUARIO_MODIFICAR_CORRECTO,
    USUARIO_MODIFICAR_INCORRECTO,
    ESPECTACULO_CREAR_INTENTO,
    ESPECTACULO_CREAR_CORRECTO,
    ESPECTACULO_CREAR_INCORRECTO,
    ENTRADAS_VER_ESTADO,
    ENTRADAS_MOSTRAR_ESTADO,
    ENTRADAS_ANULAR_MOSTRAR_DISPONIBLES,
    ENTRADAS_ANULAR_INTENTO,
    ENTRADAS_ANULAR_CORRECTO,
    ENTRADAS_ANULAR_INCORRECTO,
    ENTRADAS_RESERVAR_MOSTRAR_DISPONIBLES,
    ENTRADAS_RESERVAR_INTENTO,
    ENTRADAS_RESERVAR_CORRECTO,
    ENTRADAS_RESERVAR_INCORRECTO,
}
