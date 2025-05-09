package com.elingenio.Proyecto.Controller;

import com.elingenio.Proyecto.Modelo.Vendedor;
import com.elingenio.Proyecto.Services.VendedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/vendedores")
public class VendedorController {

    @Autowired
    private VendedorService vendedorService;

    @GetMapping
    public String listarVendedores(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<Vendedor> vendedoresPage = vendedorService.obtenerTodosPaginado(page, size);
        model.addAttribute("vendedores", vendedoresPage.getContent());
        model.addAttribute("page", vendedoresPage);
        return "vistas/vendedores/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoVendedor(Model model) {
        model.addAttribute("vendedor", new Vendedor());
        return "vistas/vendedores/formulario";
    }

    @PostMapping
    public String guardarVendedor(@ModelAttribute("vendedor") Vendedor vendedor) {
        vendedorService.guardarVendedor(vendedor);
        return "redirect:/vendedores";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarVendedor(@PathVariable Long id, Model model) {
        Vendedor vendedor = vendedorService.obtenerPorId(id).orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("vendedor", vendedor);
        return "vistas/vendedores/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarVendedor(@PathVariable Long id) {
        vendedorService.eliminarVendedor(id);
        return "redirect:/vendedores";
    }
}