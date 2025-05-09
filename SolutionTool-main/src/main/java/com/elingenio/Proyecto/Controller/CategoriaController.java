package com.elingenio.Proyecto.Controller;

import com.elingenio.Proyecto.Modelo.Categoria;
import com.elingenio.Proyecto.Services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public String listarCategorias(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Categoria> categorias;
        if (search != null && !search.isEmpty()) {
            categorias = categoriaService.buscarPorNombre(search, pageable);
        } else {
            categorias = categoriaService.getAllCategoriasPaginadas(pageable);
        }
        model.addAttribute("categorias", categorias.getContent());
        model.addAttribute("categoriasPage", categorias);
        model.addAttribute("search", search);
        return "vistas/categoria/listar";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "vistas/categoria/crear";
    }

    @PostMapping
    public String guardarCategoria(@ModelAttribute("categoria") Categoria categoria) {
        categoriaService.saveCategoria(categoria);
        return "redirect:/categorias";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Optional<Categoria> categoria = categoriaService.getCategoriaById(id);
        if (categoria.isPresent()) {
            model.addAttribute("categoria", categoria.get());
            return "vistas/categoria/editar";
        } else {
            return "redirect:/categorias";
        }
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarCategoria(@PathVariable Long id, @ModelAttribute("categoria") Categoria categoria) {
        categoria.setIdCategoria(id);
        categoriaService.saveCategoria(categoria);
        return "redirect:/categorias";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id) {
        categoriaService.deleteCategoria(id);
        return "redirect:/categorias";
    }
}