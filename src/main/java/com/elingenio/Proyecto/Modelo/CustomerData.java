package com.elingenio.Proyecto.Modelo;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CustomerData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoriaCliente;
    private Double confianza; // New field for confidence
    private int edad;
    private int frecuenciaCompra;
    private double valorTotalCompra;
    private int ultimaCompra;
    private String metodoPago;

    // Constructors
    public CustomerData() {}

    public CustomerData(String categoriaCliente, Double confianza, int edad, int frecuenciaCompra, double valorTotalCompra, int ultimaCompra, String metodoPago) {
        this.categoriaCliente = categoriaCliente;
        this.confianza = confianza;
        this.edad = edad;
        this.frecuenciaCompra = frecuenciaCompra;
        this.valorTotalCompra = valorTotalCompra;
        this.ultimaCompra = ultimaCompra;
        this.metodoPago = metodoPago;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoriaCliente() {
        return categoriaCliente;
    }

    public void setCategoriaCliente(String categoriaCliente) {
        this.categoriaCliente = categoriaCliente;
    }

    public Double getConfianza() {
        return confianza;
    }

    public void setConfianza(Double confianza) {
        this.confianza = confianza;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public int getFrecuenciaCompra() {
        return frecuenciaCompra;
    }

    public void setFrecuenciaCompra(int frecuenciaCompra) {
        this.frecuenciaCompra = frecuenciaCompra;
    }

    public double getValorTotalCompra() {
        return valorTotalCompra;
    }

    public void setValorTotalCompra(double valorTotalCompra) {
        this.valorTotalCompra = valorTotalCompra;
    }

    public int getUltimaCompra() {
        return ultimaCompra;
    }

    public void setUltimaCompra(int ultimaCompra) {
        this.ultimaCompra = ultimaCompra;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    @Override
    public String toString() {
        return "CustomerData{" +
                "id=" + id +
                ", categoriaCliente='" + categoriaCliente + '\'' +
                ", confianza=" + confianza +
                ", edad=" + edad +
                ", frecuenciaCompra=" + frecuenciaCompra +
                ", valorTotalCompra=" + valorTotalCompra +
                ", ultimaCompra=" + ultimaCompra +
                ", metodoPago='" + metodoPago + '\'' +
                '}';
    }
}