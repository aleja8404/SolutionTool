package com.elingenio.Proyecto.Repository;

import com.elingenio.Proyecto.Modelo.Proveedor;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    Optional<Proveedor> findByCorreoElectronico(String email);
}
