package com.elingenio.Proyecto.Modelo;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "compras")
public class Compra implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCompra;

    @ManyToOne
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;

    @ManyToOne
    @JoinColumn(name = "id_vendedor", nullable = false)
    private Vendedor vendedor;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime fechaDeCompra; // Keep as LocalDateTime
    private BigDecimal importeTotal;

    private LocalDateTime fechaDeCreacion;
    private LocalDateTime fechaDeActualizacion;
}