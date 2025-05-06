package com.elingenio.Proyecto.Controller;

import com.elingenio.Proyecto.Modelo.Proveedor;
import com.elingenio.Proyecto.Services.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/proveedores")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    @GetMapping
    public String listarProveedores(Model model, 
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Proveedor> proveedoresPage = proveedorService.obtenerTodosPaginado(page, size);
        model.addAttribute("proveedores", proveedoresPage.getContent());
        model.addAttribute("page", proveedoresPage);
        return "vistas/proveedores/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoProveedor(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        return "vistas/proveedores/formulario";
    }

    @PostMapping
    public String guardarProveedor(@ModelAttribute Proveedor proveedor) {
        proveedorService.guardarProveedor(proveedor);
        return "redirect:/proveedores";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarProveedor(@PathVariable Long id, Model model) {
        Optional<Proveedor> proveedor = proveedorService.obtenerPorId(id);
        if (proveedor.isPresent()) {
            model.addAttribute("proveedor", proveedor.get());
            return "vistas/proveedores/formulario";
        } else {
            return "redirect:/proveedores";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProveedor(@PathVariable Long id) {
        proveedorService.eliminarProveedor(id);
        return "redirect:/proveedores";
    }
}