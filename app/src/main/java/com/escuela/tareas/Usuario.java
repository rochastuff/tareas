package com.escuela.tareas;

public class Usuario {

    public String usuario;
    public String tutor;
    public int puntos;

    public Usuario() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Usuario(String usuario, String tutor) {
        this.usuario = usuario;
        this.tutor = tutor;
        this.puntos = 0;
    }
}
