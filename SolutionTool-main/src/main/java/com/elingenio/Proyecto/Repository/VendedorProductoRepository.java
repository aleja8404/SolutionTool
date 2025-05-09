package com.elingenio.Proyecto.Repository;

import com.elingenio.Proyecto.Modelo.VendedorProducto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendedorProductoRepository extends JpaRepository<VendedorProducto, Long> {

    List<VendedorProducto> findByVendedorIdVendedor(Long idVendedor);

}
