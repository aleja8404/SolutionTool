package com.elingenio.Proyecto.Services;

import com.elingenio.Proyecto.Modelo.Categoria;
import com.elingenio.Proyecto.Repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public Page<Categoria> getAllCategoriasPaginadas(Pageable pageable) {
        return categoriaRepository.findAll(pageable);
    }

    public Page<Categoria> buscarPorNombre(String search, Pageable pageable) {
        return categoriaRepository.findByNombreContainingIgnoreCase(search, pageable);
    }

    public List<Categoria> getAllCategorias() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> getCategoriaById(Long id) {
        return categoriaRepository.findById(id);
    }

    public Categoria saveCategoria(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public void deleteCategoria(Long id) {
        categoriaRepository.deleteById(id);
    }
}