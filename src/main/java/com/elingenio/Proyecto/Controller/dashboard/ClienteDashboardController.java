package com.elingenio.Proyecto.Controller.dashboard;

import com.elingenio.Proyecto.Modelo.Cliente;
import com.elingenio.Proyecto.Modelo.OrderItem;
import com.elingenio.Proyecto.Modelo.Producto;
import com.elingenio.Proyecto.Services.ClienteService;
import com.elingenio.Proyecto.Services.OrderItemService;
import com.elingenio.Proyecto.Services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.web.bind.support.SessionStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cliente")
@PreAuthorize("hasRole('CLIENTE')")
@SessionAttributes("carrito")
public class ClienteDashboardController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ProductoService productoService;

    @ModelAttribute("carrito")
    public List<OrderItem> inicializarCarrito() {
        return new ArrayList<>();
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model, Authentication authentication) {
        String correoElectronico = authentication.getName();
        Optional<Cliente> clienteOpt = clienteService.obtenerPorCorreoElectronico(correoElectronico);
        if (clienteOpt.isEmpty()) {
            return "redirect:/cliente/setup";
        }
        Cliente cliente = clienteOpt.get();
        model.addAttribute("cliente", cliente);
        List<OrderItem> ordenes = orderItemService.obtenerPorCliente(cliente.getIdCliente());
        model.addAttribute("ordenes", ordenes);
        return "vistas/clientes/dashboard";
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(Model model, Authentication authentication) {
        String correoElectronico = authentication.getName();
        Optional<Cliente> clienteOpt = clienteService.obtenerPorCorreoElectronico(correoElectronico);
        if (clienteOpt.isEmpty()) {
            return "redirect:/cliente/setup";
        }
        model.addAttribute("cliente", clienteOpt.get());
        return "vistas/clientes/perfil";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(@ModelAttribute("cliente") Cliente cliente, Authentication authentication) {
        String correoElectronico = authentication.getName();
        Cliente existingCliente = clienteService.obtenerPorCorreoElectronico(correoElectronico)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        cliente.setIdCliente(existingCliente.getIdCliente());
        cliente.setCorreoElectronico(correoElectronico);
        clienteService.guardarCliente(cliente);
        return "redirect:/cliente/perfil";
    }

    @GetMapping("/ordenes")
    public String mostrarOrdenes(Model model, Authentication authentication) {
        String correoElectronico = authentication.getName();
        Optional<Cliente> clienteOpt = clienteService.obtenerPorCorreoElectronico(correoElectronico);
        if (clienteOpt.isEmpty()) {
            return "redirect:/cliente/setup";
        }
        Cliente cliente = clienteOpt.get();
        List<OrderItem> ordenes = orderItemService.obtenerPorCliente(cliente.getIdCliente());
        model.addAttribute("ordenes", ordenes);
        model.addAttribute("cliente", cliente);
        return "vistas/clientes/ordenes";
    }

    @GetMapping("/productos")
    public String mostrarProductos(Model model, Authentication authentication) {
        String correoElectronico = authentication.getName();
        Optional<Cliente> clienteOpt = clienteService.obtenerPorCorreoElectronico(correoElectronico);
        if (clienteOpt.isEmpty()) {
            return "redirect:/cliente/setup";
        }
        model.addAttribute("cliente", clienteOpt.get());
        model.addAttribute("productos", productoService.obtenerTodos());
        return "vistas/clientes/productos";
    }

    @PostMapping("/carrito/agregar")
    public String agregarAlCarrito(@RequestParam("productoId") Long productoId, 
                                   @RequestParam("cantidad") int cantidad, 
                                   @ModelAttribute("carrito") List<OrderItem> carrito, 
                                   Authentication authentication) {
        String correoElectronico = authentication.getName();
        Optional<Cliente> clienteOpt = clienteService.obtenerPorCorreoElectronico(correoElectronico);
        if (clienteOpt.isEmpty()) {
            return "redirect:/cliente/setup";
        }
        Producto producto = productoService.obtenerPorId(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        if (cantidad <= 0) {
            return "redirect:/cliente/productos"; // Prevent invalid quantities
        }
        OrderItem item = new OrderItem();
        item.setProducto(producto);
        item.setCantidad(cantidad);
        item.setPrecio(producto.getPrecio());
        if (item.getProducto() != null && item.getPrecio() != null) { // Safety check
            carrito.add(item);
        }
        return "redirect:/cliente/carrito";
    }

    @GetMapping("/carrito")
    public String mostrarCarrito(Model model, @ModelAttribute("carrito") List<OrderItem> carrito, 
                                 Authentication authentication) {
        String correoElectronico = authentication.getName();
        Optional<Cliente> clienteOpt = clienteService.obtenerPorCorreoElectronico(correoElectronico);
        if (clienteOpt.isEmpty()) {
            return "redirect:/cliente/setup";
        }
        model.addAttribute("cliente", clienteOpt.get());
        model.addAttribute("carrito", carrito);
        BigDecimal total = carrito.stream()
            .filter(item -> item != null && item.getPrecio() != null) // Filter out nulls
            .map(item -> item.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("totalCarrito", total);
        return "vistas/clientes/carrito";
    }


    @PostMapping("/carrito/actualizar")
public String actualizarCarrito(@RequestParam("productoId") Long productoId, 
                                @RequestParam("cantidad") int cantidad, 
                                @ModelAttribute("carrito") List<OrderItem> carrito, 
                                RedirectAttributes redirectAttributes) {
    if (cantidad <= 0) {
        redirectAttributes.addFlashAttribute("error", "La cantidad debe ser mayor que 0.");
        return "redirect:/cliente/carrito";
    }
    for (OrderItem item : carrito) {
        if (item.getProducto().getIdProducto().equals(productoId)) {
            item.setCantidad(cantidad);
            break;
        }
    }
    return "redirect:/cliente/carrito";
}

@PostMapping("/carrito/eliminar")
public String eliminarDelCarrito(@RequestParam("productoId") Long productoId, 
                                @ModelAttribute("carrito") List<OrderItem> carrito) {
    carrito.removeIf(item -> item.getProducto().getIdProducto().equals(productoId));
    return "redirect:/cliente/carrito";
}

    
    @PostMapping("/carrito/comprar")
    public String realizarCompra(@ModelAttribute("carrito") List<OrderItem> carrito, 
                                 Authentication authentication, 
                                 SessionStatus sessionStatus, 
                                 RedirectAttributes redirectAttributes) {
        String correoElectronico = authentication.getName();
        Optional<Cliente> clienteOpt = clienteService.obtenerPorCorreoElectronico(correoElectronico);
        if (clienteOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No estás autenticado o tu cuenta no está configurada.");
            return "redirect:/cliente/carrito";
        }
        if (carrito.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El carrito está vacío.");
            return "redirect:/cliente/carrito";
        }
        try {
            Cliente cliente = clienteOpt.get();
            orderItemService.guardarOrdenes(cliente, carrito);
            carrito.clear();
            sessionStatus.setComplete();
            redirectAttributes.addFlashAttribute("success", "Compra realizada con éxito.");
            return "redirect:/cliente/ordenes";
        } catch (Exception e) {
            String errorMsg = "Error al procesar la compra. Por favor, intenta de nuevo.";
            if (e.getMessage().contains("Default vendedor not found")) {
                errorMsg = "Error del sistema: No se encontró el vendedor predeterminado. Contacta al soporte.";
            }
            redirectAttributes.addFlashAttribute("error", errorMsg);
            return "redirect:/cliente/carrito";
        }
    }
}