package com.escuela.tareas;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Tarea {

    public String tareaId;
    public String descripcion;
    public Boolean terminada;
    public String alumnoEmail;
    public String tutorEmail;

    public Boolean puntoDado;
    public Tarea() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Tarea(String tareaId, String descripcion, String alumnoEmail, String tutorEmail, Boolean terminada, Boolean puntoDado) {
        this.tareaId = tareaId;
        this.descripcion = descripcion;
        this.terminada = terminada;
        this.alumnoEmail = alumnoEmail;
        this.tutorEmail = tutorEmail;
        this.puntoDado = puntoDado;
    }

}
