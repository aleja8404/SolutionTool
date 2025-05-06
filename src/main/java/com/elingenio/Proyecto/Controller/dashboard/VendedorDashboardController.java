
package com.elingenio.Proyecto.Controller.dashboard;

import com.elingenio.Proyecto.Modelo.Compra;
import com.elingenio.Proyecto.Modelo.Vendedor;
import com.elingenio.Proyecto.Modelo.Venta;
import com.elingenio.Proyecto.Modelo.VendedorProducto;
import com.elingenio.Proyecto.Services.ClienteService;
import com.elingenio.Proyecto.Services.CompraService;
import com.elingenio.Proyecto.Services.ProductoService;
import com.elingenio.Proyecto.Services.ProveedorService;
import com.elingenio.Proyecto.Services.VentaService;
import com.elingenio.Proyecto.Services.VendedorService;
import com.elingenio.Proyecto.Repository.VendedorProductoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/vendedor")
@PreAuthorize("hasRole('VENDEDOR')")
public class VendedorDashboardController {

    @Autowired
    private VendedorService vendedorService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private VentaService ventaService;

    @Autowired
    private CompraService compraService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private VendedorProductoRepository vendedorProductoRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        String email = authentication.getName();
        Optional<Vendedor> vendedorOpt = vendedorService.findByCorreoElectronico(email);
        if (vendedorOpt.isEmpty()) {
            return "redirect:/vendedor/setup";
        }
        Vendedor vendedor = vendedorOpt.get();
        model.addAttribute("vendedor", vendedor);
        model.addAttribute("vendedorProductos", vendedorProductoRepository.findByVendedorIdVendedor(vendedor.getIdVendedor()));
        model.addAttribute("ventas", ventaService.findByVendedorId(vendedor.getIdVendedor()));
        model.addAttribute("compras", compraService.findByVendedorId(vendedor.getIdVendedor()));
        return "vendedor/dashboard";
    }

    @GetMapping("/setup")
    public String mostrarFormularioSetup(Model model, Authentication authentication) {
        String email = authentication.getName();
        Optional<Vendedor> vendedorOpt = vendedorService.findByCorreoElectronico(email);
        if (vendedorOpt.isPresent()) {
            return "redirect:/vendedor/dashboard";
        }
        Vendedor vendedor = new Vendedor();
        vendedor.setCorreoElectronico(email);
        model.addAttribute("vendedor", vendedor);
        return "vendedor/setup";
    }

    @PostMapping("/setup")
    public String guardarSetup(@Valid @ModelAttribute("vendedor") Vendedor vendedor, BindingResult result, Authentication authentication) {
        if (result.hasErrors()) {
            return "vendedor/setup";
        }
        String email = authentication.getName();
        vendedor.setCorreoElectronico(email);
        vendedorService.guardarVendedor(vendedor);
        return "redirect:/vendedor/dashboard";
    }

    @GetMapping("/editar")
    public String mostrarFormularioEditar(Model model, Authentication authentication) {
        String email = authentication.getName();
        Optional<Vendedor> vendedorOpt = vendedorService.findByCorreoElectronico(email);
        if (vendedorOpt.isEmpty()) {
            return "redirect:/vendedor/setup";
        }
        model.addAttribute("vendedor", vendedorOpt.get());
        return "vendedor/editar-perfil";
    }

    @PostMapping("/editar")
    public String guardarEditar(@Valid @ModelAttribute("vendedor") Vendedor vendedor, BindingResult result, Authentication authentication) {
        if (result.hasErrors()) {
            return "vendedor/editar-perfil";
        }
        String email = authentication.getName();
        Optional<Vendedor> vendedorOpt = vendedorService.findByCorreoElectronico(email);
        if (vendedorOpt.isPresent()) {
            Vendedor existingVendedor = vendedorOpt.get();
            existingVendedor.setNombre(vendedor.getNombre());
            existingVendedor.setTelefono(vendedor.getTelefono());
            existingVendedor.setDireccion(vendedor.getDireccion());
            vendedorService.guardarVendedor(existingVendedor);
        }
        return "redirect:/vendedor/dashboard";
    }

    @GetMapping("/ventas/nueva")
    public String mostrarFormularioVenta(Model model, Authentication authentication) {
        String email = authentication.getName();
        Optional<Vendedor> vendedorOpt = vendedorService.findByCorreoElectronico(email);
        if (vendedorOpt.isEmpty()) {
            return "redirect:/vendedor/setup";
        }
        Venta venta = new Venta();
        venta.setVendedor(vendedorOpt.get());
        venta.setFechaDelPedido(LocalDateTime.now());
        model.addAttribute("venta", venta);
        model.addAttribute("clientes", clienteService.obtenerTodos());
        return "vendedor/venta-form";
    }

    @PostMapping("/ventas")
    public String guardarVenta(@Valid @ModelAttribute("venta") Venta venta, BindingResult result, Model model, Authentication authentication) {
        if (result.hasErrors()) {
            model.addAttribute("clientes", clienteService.obtenerTodos());
            return "vendedor/venta-form";
        }
        String email = authentication.getName();
        Optional<Vendedor> vendedorOpt = vendedorService.findByCorreoElectronico(email);
        if (vendedorOpt.isPresent()) {
            venta.setVendedor(vendedorOpt.get());
            ventaService.registrarVenta(venta);
        }
        return "redirect:/vendedor/dashboard";
    }

    @GetMapping("/compras/nueva")
    public String mostrarFormularioCompra(Model model, Authentication authentication) {
        String email = authentication.getName();
        Optional<Vendedor> vendedorOpt = vendedorService.findByCorreoElectronico(email);
        if (vendedorOpt.isEmpty()) {
            return "redirect:/vendedor/setup";
        }
        Compra compra = new Compra();
        compra.setVendedor(vendedorOpt.get());
        compra.setFechaDeCompra(LocalDateTime.now());
        model.addAttribute("compra", compra);
        model.addAttribute("proveedores", proveedorService.obtenerTodos());
        return "vendedor/compra-form";
    }

    @PostMapping("/compras")
    public String guardarCompra(@Valid @ModelAttribute("compra") Compra compra, BindingResult result, Model model, Authentication authentication) {
        if (result.hasErrors()) {
            model.addAttribute("proveedores", proveedorService.obtenerTodos());
            return "vendedor/compra-form";
        }
        String email = authentication.getName();
        Optional<Vendedor> vendedorOpt = vendedorService.findByCorreoElectronico(email);
        if (vendedorOpt.isPresent()) {
            compra.setVendedor(vendedorOpt.get());
            compraService.registrarCompra(compra);
        }
        return "redirect:/vendedor/dashboard";
    }

    @GetMapping("/productos/nuevo")
    public String mostrarFormularioProducto(Model model, Authentication authentication) {
        String email = authentication.getName();
        Optional<Vendedor> vendedorOpt = vendedorService.findByCorreoElectronico(email);
        if (vendedorOpt.isEmpty()) {
            return "redirect:/vendedor/setup";
        }
        VendedorProducto vendedorProducto = new VendedorProducto();
        vendedorProducto.setVendedor(vendedorOpt.get());
        model.addAttribute("vendedorProducto", vendedorProducto);
        model.addAttribute("productos", productoService.obtenerTodos());
        return "vendedor/producto-form";
    }

    @PostMapping("/productos")
    public String guardarProducto(@Valid @ModelAttribute("vendedorProducto") VendedorProducto vendedorProducto, BindingResult result, Model model, Authentication authentication) {
        if (result.hasErrors()) {
            model.addAttribute("productos", productoService.obtenerTodos());
            return "vendedor/producto-form";
        }
        String email = authentication.getName();
        Optional<Vendedor> vendedorOpt = vendedorService.findByCorreoElectronico(email);
        if (vendedorOpt.isPresent()) {
            vendedorProducto.setVendedor(vendedorOpt.get());
            vendedorProductoRepository.save(vendedorProducto);
        }
        return "redirect:/vendedor/dashboard";
    }

    @GetMapping("/productos/editar/{id}")
    public String mostrarFormularioEditarProducto(@PathVariable("id") Long id, Model model, Authentication authentication) {
        String email = authentication.getName();
        Optional<Vendedor> vendedorOpt = vendedorService.findByCorreoElectronico(email);
        if (vendedorOpt.isEmpty()) {
            return "redirect:/vendedor/setup";
        }
        Optional<VendedorProducto> vendedorProductoOpt = vendedorProductoRepository.findById(id);
        if (vendedorProductoOpt.isEmpty() || !vendedorProductoOpt.get().getVendedor().getIdVendedor().equals(vendedorOpt.get().getIdVendedor())) {
            return "redirect:/vendedor/dashboard";
        }
        model.addAttribute("vendedorProducto", vendedorProductoOpt.get());
        model.addAttribute("productos", productoService.obtenerTodos());
        return "vendedor/producto-form";
    }

    @PostMapping("/productos/actualizar/{id}")
    public String actualizarProducto(@PathVariable("id") Long id, @Valid @ModelAttribute("vendedorProducto") VendedorProducto vendedorProducto, BindingResult result, Model model, Authentication authentication) {
        if (result.hasErrors()) {
            model.addAttribute("productos", productoService.obtenerTodos());
            return "vendedor/producto-form";
        }
        String email = authentication.getName();
        Optional<Vendedor> vendedorOpt = vendedorService.findByCorreoElectronico(email);
        if (vendedorOpt.isEmpty()) {
            return "redirect:/vendedor/setup";
        }
        Optional<VendedorProducto> existingProductoOpt = vendedorProductoRepository.findById(id);
        if (existingProductoOpt.isEmpty() || !existingProductoOpt.get().getVendedor().getIdVendedor().equals(vendedorOpt.get().getIdVendedor())) {
            return "redirect:/vendedor/dashboard";
        }
        VendedorProducto existingProducto = existingProductoOpt.get();
        existingProducto.setProducto(vendedorProducto.getProducto());
        existingProducto.setCantidad(vendedorProducto.getCantidad());
        existingProducto.setPrecio(vendedorProducto.getPrecio());
        vendedorProductoRepository.save(existingProducto);
        return "redirect:/vendedor/dashboard";
    }

    @GetMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable("id") Long id, Authentication authentication) {
        String email = authentication.getName();
        Optional<Vendedor> vendedorOpt = vendedorService.findByCorreoElectronico(email);
        if (vendedorOpt.isEmpty()) {
            return "redirect:/vendedor/setup";
        }
        Optional<VendedorProducto> vendedorProductoOpt = vendedorProductoRepository.findById(id);
        if (vendedorProductoOpt.isPresent() && vendedorProductoOpt.get().getVendedor().getIdVendedor().equals(vendedorOpt.get().getIdVendedor())) {
            vendedorProductoRepository.deleteById(id);
        }
        return "redirect:/vendedor/dashboard";
    }
}
