package modelo;

import java.io.Serializable;

/**
 * Sala existe de manera independiente al evento.
 * Por eso, en el modelo se usa como una agregación desde EventoUniversitario.
 */
public class Sala implements Serializable{
    private int id;
    private String nombre;
    private int capacidad;

    public Sala(int id, String nombre, int capacidad) {
        this.id = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return;
        }
        this.nombre = nombre;
    }
}
