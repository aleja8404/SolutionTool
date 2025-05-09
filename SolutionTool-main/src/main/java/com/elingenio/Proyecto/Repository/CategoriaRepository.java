package com.elingenio.Proyecto.Repository;

import com.elingenio.Proyecto.Modelo.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Page<Categoria> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}