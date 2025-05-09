package com.elingenio.Proyecto.Modelo;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "vendedores")
public class Vendedor  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVendedor;

    private String nombre;
    private String correoElectronico;
    private String telefono;
    private String direccion;

    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
