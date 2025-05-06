package com.elingenio.Proyecto.Repository;

import com.elingenio.Proyecto.Modelo.Vendedor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendedorRepository extends JpaRepository<Vendedor, Long> {

    Optional<Vendedor> findByCorreoElectronico(String correoElectronico);

    // Optional: If needed for other operations
    List<Vendedor> findByIdVendedor(Long idVendedor);

}
