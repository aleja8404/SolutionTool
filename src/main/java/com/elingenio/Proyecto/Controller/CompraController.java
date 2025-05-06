package com.elingenio.Proyecto.Controller;

import com.elingenio.Proyecto.Modelo.Compra;
import com.elingenio.Proyecto.Services.CompraService;
import com.elingenio.Proyecto.Services.ProveedorService;
import com.elingenio.Proyecto.Services.VendedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/compras")
public class CompraController {

    @Autowired
    private CompraService compraService;

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private VendedorService vendedorService;

    @GetMapping
    public String listarCompras(Model model) {
        List<Compra> compras = compraService.obtenerTodas();
        System.out.println("Compras enviadas a la vista: " + compras.size());
        model.addAttribute("compras", compras);
        model.addAttribute("proveedores", proveedorService.obtenerTodos());
        model.addAttribute("vendedores", vendedorService.obtenerTodos());
        return "vistas/compras/lista";
    }
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevaCompra(Model model) {
        model.addAttribute("compra", new Compra());
        model.addAttribute("proveedores", proveedorService.obtenerTodos());
        model.addAttribute("vendedores", vendedorService.obtenerTodos());
        return "vistas/compras/formulario";
    }

    @PostMapping
    public String registrarCompra(@ModelAttribute Compra compra) {
        compraService.registrarCompra(compra);
        return "redirect:/compras";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarCompra(@PathVariable Long id, Model model) {
        Optional<Compra> compra = compraService.obtenerPorId(id);
        if (compra.isPresent()) {
            model.addAttribute("compra", compra.get());
            model.addAttribute("proveedores", proveedorService.obtenerTodos());
            model.addAttribute("vendedores", vendedorService.obtenerTodos());
            return "vistas/compras/formulario";
        } else {
            return "redirect:/compras";
        }
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarCompra(@PathVariable Long id, @ModelAttribute Compra compra) {
        compra.setIdCompra(id);
        compraService.registrarCompra(compra);
        return "redirect:/compras";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCompra(@PathVariable Long id) {
        compraService.eliminarCompra(id);
        return "redirect:/compras";
    }
}