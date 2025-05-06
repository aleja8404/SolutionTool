package com.elingenio.Proyecto.Modelo;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "ventas")
public class Venta implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVenta;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_vendedor", nullable = false)
    private Vendedor vendedor;

    private LocalDateTime fechaDelPedido;
    private BigDecimal total;
    private String estado; // COMPLETADA, PENDIENTE, CANCELADA
    private String deviceType; // mobile, desktop, tablet

    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}