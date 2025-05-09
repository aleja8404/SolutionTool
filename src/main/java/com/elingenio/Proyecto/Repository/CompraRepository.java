package com.elingenio.Proyecto.Repository;


import com.elingenio.Proyecto.Modelo.Compra;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    List<Compra> findByVendedorIdVendedor(Long vendedorId);
}
