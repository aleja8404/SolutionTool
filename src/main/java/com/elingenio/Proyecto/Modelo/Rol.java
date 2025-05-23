package com.elingenio.Proyecto.Modelo;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "rol")
public class Rol implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Rol(Long id, String nombre) {
        super();
        this.id = id;
        this.nombre = nombre;
    }

    public Rol() {

    }

    public Rol(String nombre) {
        super();
        this.nombre = nombre;
    }


}