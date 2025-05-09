package com.elingenio.Proyecto.Services;

import com.elingenio.Proyecto.Modelo.Cliente;
import com.elingenio.Proyecto.Modelo.OrderItem;
import com.elingenio.Proyecto.Modelo.Vendedor;
import com.elingenio.Proyecto.Modelo.Venta;
import com.elingenio.Proyecto.Repository.OrderItemRepository;
import com.elingenio.Proyecto.Repository.VentaRepository;
import com.elingenio.Proyecto.Repository.VendedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private VendedorRepository vendedorRepository;

    @Transactional
    public void guardarOrdenes(Cliente cliente, List<OrderItem> items) {
        Venta venta = new Venta();
        venta.setCliente(cliente);

        // Try to find default vendedor, create if not found
        Optional<Vendedor> vendedorOpt = vendedorRepository.findByCorreoElectronico("sistema@ferreteria.com");
        Vendedor defaultVendedor;
        if (vendedorOpt.isEmpty()) {
            defaultVendedor = new Vendedor();
            defaultVendedor.setCorreoElectronico("sistema@ferreteria.com");
            defaultVendedor.setNombre("Sistema");
            // Set other required fields as needed
            defaultVendedor = vendedorRepository.save(defaultVendedor);
        } else {
            defaultVendedor = vendedorOpt.get();
        }

        venta.setVendedor(defaultVendedor);
        venta.setFechaDelPedido(LocalDateTime.now());
        venta.setTotal(items.stream()
            .map(item -> item.getPrecio().multiply(new java.math.BigDecimal(item.getCantidad())))
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        venta.setEstado("PENDIENTE");
        ventaRepository.save(venta);
        items.forEach(item -> item.setVenta(venta));
        orderItemRepository.saveAll(items);
    }

    public List<OrderItem> obtenerTodos() {
        return orderItemRepository.findAll();
    }

    public Optional<OrderItem> obtenerPorId(Long id) {
        return orderItemRepository.findById(id);
    }

    public OrderItem guardarOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    public List<OrderItem> obtenerPorCliente(Long clienteId) {
        return orderItemRepository.findByVentaClienteIdCliente(clienteId);
    }

    public void eliminarOrderItem(Long id) {
        orderItemRepository.deleteById(id);
    }

    public Page<OrderItem> obtenerTodosPaginados(Pageable pageable) {
        return orderItemRepository.findAll(pageable);
    }
}