package com.elingenio.Proyecto.Modelo;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "vendedores_productos")
public class VendedorProducto implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVendedorProducto;

    @ManyToOne
    @JoinColumn(name = "id_vendedor", nullable = false)
    private Vendedor vendedor;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    private Integer cantidad; // Added
    private BigDecimal precio; // Added
}