package com.elingenio.Proyecto.Services;

import com.elingenio.Proyecto.Modelo.OrderItem;
import com.elingenio.Proyecto.Modelo.Venta;
import com.elingenio.Proyecto.Repository.VentaRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua_parser.Parser;
import ua_parser.Client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class VentaService {

    private static final Logger logger = LoggerFactory.getLogger(VentaService.class);

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private HttpServletRequest request;

    public Page<Venta> obtenerTodasPaginadas(Pageable pageable) {
        return ventaRepository.findAll(pageable);
    }

    public List<Venta> obtenerTodas() {
        return ventaRepository.findAll();
    }

    public Optional<Venta> obtenerPorId(Long id) {
        return ventaRepository.findById(id);
    }

    public Venta registrarVenta(Venta venta) {
        String deviceType = detectDeviceType();
        venta.setDeviceType(deviceType);
        logger.info("Saving venta with deviceType: {}", deviceType);
        return ventaRepository.save(venta);
    }

    public Venta actualizarVenta(Venta venta) {
        String deviceType = detectDeviceType();
        venta.setDeviceType(deviceType);
        logger.info("Updating venta with deviceType: {}", deviceType);
        return ventaRepository.save(venta);
    }

    private String detectDeviceType() {
        String userAgent = request.getHeader("User-Agent");
        Parser uaParser = new Parser();
        Client client = uaParser.parse(userAgent);
        String deviceType = "desktop";
        if (client.device.family != null) {
            String family = client.device.family.toLowerCase();
            if (family.contains("mobile") || family.contains("phone") || family.contains("android") || family.contains("iphone")) {
                deviceType = "mobile";
            } else if (family.contains("tablet") || family.contains("ipad") || family.contains("kindle")) {
                deviceType = "tablet";
            }
        }
        return deviceType;
    }

    public List<Venta> findByVendedorId(Long vendedorId) {
        return ventaRepository.findByVendedorIdVendedor(vendedorId);
    }

    public List<OrderItem> findOrderItemsByVentaId(Long ventaId) {
        return ventaRepository.findOrderItemsByVentaId(ventaId);
    }

    public void eliminarVenta(Long id) {
        ventaRepository.deleteById(id);
    }

    public Map<String, Long> obtenerEstadoCartera() {
        Map<String, Long> estadoCartera = new HashMap<>();
        long completada = ventaRepository.countByEstado("COMPLETADA");
        long pendiente = ventaRepository.countByEstado("PENDIENTE");
        long cancelada = ventaRepository.countByEstado("CANCELADA");

        estadoCartera.put("completada", completada);
        estadoCartera.put("pendiente", pendiente);
        estadoCartera.put("cancelada", cancelada);

        logger.info("Estado de Cartera - Completada: {}, Pendiente: {}, Cancelada: {}",
                completada, pendiente, cancelada);

        return estadoCartera;
    }

    public Map<String, Long> obtenerTraficoPorDispositivo() {
        Map<String, Long> trafico = new HashMap<>();
        long mobile = ventaRepository.countByDeviceType("mobile");
        long desktop = ventaRepository.countByDeviceType("desktop");
        long tablet = ventaRepository.countByDeviceType("tablet");

        trafico.put("mobile", mobile);
        trafico.put("desktop", desktop);
        trafico.put("tablet", tablet);

        logger.info("Tráfico por Dispositivo - Mobile: {}, Desktop: {}, Tablet: {}",
                mobile, desktop, tablet);

        return trafico;
    }

    public Map<String, List<Double>> obtenerVentasMensuales() {
        int currentYear = LocalDate.now().getYear();
        int previousYear = currentYear - 1;

        Double[] currentYearSales = new Double[12];
        Double[] previousYearSales = new Double[12];
        Arrays.fill(currentYearSales, 0.0);
        Arrays.fill(previousYearSales, 0.0);

        List<Object[]> currentYearData = ventaRepository.findMonthlySalesByYear(currentYear);
        for (Object[] data : currentYearData) {
            int month = ((Number) data[0]).intValue() - 1;
            BigDecimal total = (BigDecimal) data[1];
            currentYearSales[month] = total.doubleValue();
        }

        List<Object[]> previousYearData = ventaRepository.findMonthlySalesByYear(previousYear);
        for (Object[] data : previousYearData) {
            int month = ((Number) data[0]).intValue() - 1;
            BigDecimal total = (BigDecimal) data[1];
            previousYearSales[month] = total.doubleValue();
        }

        Map<String, List<Double>> result = new HashMap<>();
        result.put("currentYear", Arrays.asList(currentYearSales));
        result.put("previousYear", Arrays.asList(previousYearSales));

        logger.info("Ventas Mensuales - Current Year ({}): {}", currentYear, Arrays.toString(currentYearSales));
        logger.info("Ventas Mensuales - Previous Year ({}): {}", previousYear, Arrays.toString(previousYearSales));

        return result;
    }

    public long contarVentasCompletadas() {
        long count = ventaRepository.countByEstadoCompletada();
        logger.info("Contando ventas completadas: {}", count);
        return count;
    }

}