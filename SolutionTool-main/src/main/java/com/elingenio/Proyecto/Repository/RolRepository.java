package com.elingenio.Proyecto.Repository;

import com.elingenio.Proyecto.Modelo.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);
}