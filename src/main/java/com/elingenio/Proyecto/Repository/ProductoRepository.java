package com.elingenio.Proyecto.Repository;

import com.elingenio.Proyecto.Modelo.Producto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByProveedorIdProveedor(Long proveedorId);



    
}
