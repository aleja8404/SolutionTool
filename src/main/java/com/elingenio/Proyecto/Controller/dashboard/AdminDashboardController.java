package com.elingenio.Proyecto.Controller.dashboard;

import com.elingenio.Proyecto.Services.ClienteService;
import com.elingenio.Proyecto.Services.ProductoService;
import com.elingenio.Proyecto.Services.UsuarioServicio;
import com.elingenio.Proyecto.Services.VentaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class AdminDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardController.class);

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioServicio usuarioService;

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        try {
            long ventasTotal = ventaService.contarVentasCompletadas();
            long productosTotal = productoService.obtenerTodos().size();
            long usuariosTotal = usuarioService.obtenerTodo().size();
            long clientesTotal = clienteService.obtenerTodos().size();

            Map<String, Long> estadoCartera = ventaService.obtenerEstadoCartera();
            Map<String, Long> traficoDispositivo = ventaService.obtenerTraficoPorDispositivo();
            Map<String, List<Double>> ventasMensuales = ventaService.obtenerVentasMensuales();

            model.addAttribute("titulo", "Panel de Administración");
            model.addAttribute("ventasTotal", ventasTotal);
            model.addAttribute("productosTotal", productosTotal);
            model.addAttribute("usuariosTotal", usuariosTotal);
            model.addAttribute("clientesTotal", clientesTotal);

            model.addAttribute("ventasCrecimiento", calcularCrecimientoVentas());
            model.addAttribute("productosCrecimiento", calcularCrecimientoProductos());
            model.addAttribute("usuariosCrecimiento", calcularCrecimientoUsuarios());
            model.addAttribute("clientesCrecimiento", calcularCrecimientoClientes());

            long completadaCount = estadoCartera.getOrDefault("completada", 0L);
            long pendienteCount = estadoCartera.getOrDefault("pendiente", 0L);
            long canceladaCount = estadoCartera.getOrDefault("cancelada", 0L);

            model.addAttribute("completadaCount", completadaCount);
            model.addAttribute("pendienteCount", pendienteCount);
            model.addAttribute("canceladaCount", canceladaCount);

            long mobileCount = traficoDispositivo.getOrDefault("mobile", 0L);
            long desktopCount = traficoDispositivo.getOrDefault("desktop", 0L);
            long tabletCount = traficoDispositivo.getOrDefault("tablet", 0L);

            model.addAttribute("mobileCount", mobileCount);
            model.addAttribute("desktopCount", desktopCount);
            model.addAttribute("tabletCount", tabletCount);

            List<Double> currentYearSales = ventasMensuales.get("currentYear").subList(0, 6);
            List<Double> previousYearSales = ventasMensuales.get("previousYear").subList(0, 6);
            model.addAttribute("currentYearSales", currentYearSales);
            model.addAttribute("previousYearSales", previousYearSales);

            logger.info("Pasando al modelo - Completada: {}, Pendiente: {}, Cancelada: {}",
                    completadaCount, pendienteCount, canceladaCount);
            logger.info("Pasando al modelo - Mobile: {}, Desktop: {}, Tablet: {}",
                    mobileCount, desktopCount, tabletCount);
            logger.info("Pasando al modelo - Current Year Sales: {}", currentYearSales);
            logger.info("Pasando al modelo - Previous Year Sales: {}", previousYearSales);
            logger.info("Pasando al modelo - Ventas Total (Completadas): {}", ventasTotal);

        } catch (Exception e) {
            logger.error("Error al cargar el dashboard", e);
            model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
        }

        return "admin/dashboard";
    }

    private double calcularCrecimientoVentas() {
        return 10.5;
    }

    private double calcularCrecimientoProductos() {
        return 5.0;
    }

    private double calcularCrecimientoUsuarios() {
        return 8.2;
    }

    private double calcularCrecimientoClientes() {
        return 12.0;
    }
}