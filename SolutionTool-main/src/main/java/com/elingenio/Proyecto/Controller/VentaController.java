package com.elingenio.Proyecto.Controller;

import com.elingenio.Proyecto.Modelo.Venta;
import com.elingenio.Proyecto.Services.ClienteService;
import com.elingenio.Proyecto.Services.VendedorService;
import com.elingenio.Proyecto.Services.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private VendedorService vendedorService;

    @GetMapping
    public String listarVentas(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "5") int size) {
        // Create a Pageable object
        Pageable pageable = PageRequest.of(page, size);
        // Fetch paginated sales
        Page<Venta> ventasPage = ventaService.obtenerTodasPaginadas(pageable);

        // Add attributes to the model
        model.addAttribute("ventas", ventasPage.getContent());
        model.addAttribute("currentPage", ventasPage.getNumber());
        model.addAttribute("totalPages", ventasPage.getTotalPages());
        model.addAttribute("totalItems", ventasPage.getTotalElements());
        model.addAttribute("pageSize", size);

        return "vistas/ventas/lista"; // Keep original view path
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("venta", new Venta());
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("vendedores", vendedorService.obtenerTodos());
        return "vistas/ventas/formulario"; // Keep original view path
    }

    @PostMapping
    public String guardarVenta(@ModelAttribute("venta") Venta venta) {
        ventaService.registrarVenta(venta); // Sets deviceType in VentaService
        return "redirect:/ventas";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Optional<Venta> venta = ventaService.obtenerPorId(id);
        if (venta.isPresent()) {
            model.addAttribute("venta", venta.get());
            model.addAttribute("clientes", clienteService.obtenerTodos());
            model.addAttribute("vendedores", vendedorService.obtenerTodos());
            return "vistas/ventas/formulario";
        } else {
            return "redirect:/ventas";
        }
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarVenta(@PathVariable Long id, @ModelAttribute("venta") Venta venta) {
        venta.setIdVenta(id);
        ventaService.actualizarVenta(venta); // Use actualizarVenta to set deviceType
        return "redirect:/ventas";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarVenta(@PathVariable Long id) {
        ventaService.eliminarVenta(id);
        return "redirect:/ventas";
    }
}