package com.elingenio.Proyecto.Controller.dashboard;

import com.elingenio.Proyecto.Modelo.Producto;
import com.elingenio.Proyecto.Modelo.Proveedor;
import com.elingenio.Proyecto.Services.ProductoService;
import com.elingenio.Proyecto.Services.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/proveedor")
@PreAuthorize("hasRole('PROVEEDOR')")
public class ProveedorDashboardController {

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private ProductoService productoService;

    @GetMapping("/accesos/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        String email = authentication.getName();
        Optional<Proveedor> proveedorOpt = proveedorService.findByCorreoElectronico(email);

        if (proveedorOpt.isEmpty()) {
            model.addAttribute("error", "No tienes un perfil de proveedor configurado. Por favor, crea uno.");
            return "proveedor/setup";
        }

        Proveedor proveedor = proveedorOpt.get();
        model.addAttribute("proveedor", proveedor);
        model.addAttribute("productos", proveedorService.obtenerProductosPorProveedor(proveedor.getIdProveedor()));
        return "proveedor/dashboard"; // Matches C:\...\templates\proveedor\dashboard.html
    }

    @GetMapping("/accesos/setup")
    public String mostrarFormularioSetup(Model model, Authentication authentication) {
        String email = authentication.getName();
        Optional<Proveedor> proveedorOpt = proveedorService.findByCorreoElectronico(email);
        
        if (proveedorOpt.isPresent()) {
            return "redirect:/proveedor/accesos/dashboard";
        }
        
        // Create new Proveedor and set email
        Proveedor proveedor = new Proveedor();
        proveedor.setCorreoElectronico(email);
        
        // Add to model
        model.addAttribute("proveedor", proveedor);
        return "proveedor/setup";
    }

    @PostMapping("/accesos/setup")
    public String guardarSetup(@ModelAttribute("proveedor") Proveedor proveedor, Authentication authentication) {
        String email = authentication.getName();
        proveedor.setCorreoElectronico(email);
        proveedorService.guardarProveedor(proveedor);
        return "redirect:/proveedor/accesos/dashboard";
    }

    @GetMapping("/accesos/editar")
    public String mostrarFormularioEditarPerfil(Model model, Authentication authentication) {
        String email = authentication.getName();
        Proveedor proveedor = proveedorService.findByCorreoElectronico(email)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        model.addAttribute("proveedor", proveedor);
        return "proveedor/editar-perfil";
    }

    @PostMapping("/accesos/editar")
    public String actualizarPerfil(@ModelAttribute("proveedor") Proveedor proveedor, Authentication authentication) {
        String email = authentication.getName();
        Proveedor existingProveedor = proveedorService.findByCorreoElectronico(email)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        proveedor.setIdProveedor(existingProveedor.getIdProveedor());
        proveedor.setCorreoElectronico(email);
        proveedorService.guardarProveedor(proveedor);
        return "redirect:/proveedor/accesos/dashboard";
    }

    @GetMapping("/productos/nuevo")
    public String mostrarFormularioNuevoProducto(Model model, Authentication authentication) {
        String email = authentication.getName();
        Proveedor proveedor = proveedorService.findByCorreoElectronico(email)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        Producto producto = new Producto();
        producto.setProveedor(proveedor);
        model.addAttribute("producto", producto);
        return "proveedor/producto-form";
    }

    @PostMapping("/productos")
    public String guardarProducto(@ModelAttribute("producto") Producto producto, Authentication authentication) {
        String email = authentication.getName();
        Proveedor proveedor = proveedorService.findByCorreoElectronico(email)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        producto.setProveedor(proveedor);
        productoService.guardarProducto(producto);
        return "redirect:/proveedor/accesos/dashboard";
    }

    @GetMapping("/productos/editar/{id}")
    public String mostrarFormularioEditarProducto(@PathVariable Long id, Model model, Authentication authentication) {
        String email = authentication.getName();
        Proveedor proveedor = proveedorService.findByCorreoElectronico(email)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        Producto producto = productoService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        if (!producto.getProveedor().getIdProveedor().equals(proveedor.getIdProveedor())) {
            throw new RuntimeException("No tienes permiso para editar este producto");
        }
        model.addAttribute("producto", producto);
        return "proveedor/producto-form";
    }

    @PostMapping("/productos/actualizar/{id}")
    public String actualizarProducto(@PathVariable Long id, @ModelAttribute("producto") Producto producto, Authentication authentication) {
        String email = authentication.getName();
        Proveedor proveedor = proveedorService.findByCorreoElectronico(email)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        Producto existingProducto = productoService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        if (!existingProducto.getProveedor().getIdProveedor().equals(proveedor.getIdProveedor())) {
            throw new RuntimeException("No tienes permiso para actualizar este producto");
        }
        producto.setIdProducto(id);
        producto.setProveedor(proveedor);
        productoService.guardarProducto(producto);
        return "redirect:/proveedor/accesos/dashboard";
    }

    @GetMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        Proveedor proveedor = proveedorService.findByCorreoElectronico(email)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        Producto producto = productoService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        if (!producto.getProveedor().getIdProveedor().equals(proveedor.getIdProveedor())) {
            throw new RuntimeException("No tienes permiso para eliminar este producto");
        }
        productoService.eliminarProducto(id);
        return "redirect:/proveedor/accesos/dashboard";
    }
}