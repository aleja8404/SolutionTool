package com.elingenio.Proyecto.Repository;

import com.elingenio.Proyecto.Modelo.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCorreoElectronico(String correoElectronico);
    Page<Cliente> findByNombreContainingIgnoreCaseOrCorreoElectronicoContainingIgnoreCase(String nombre, String correoElectronico, Pageable pageable);
}