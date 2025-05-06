package com.elingenio.Proyecto.Controller;

import com.elingenio.Proyecto.Modelo.OrderItem;
import com.elingenio.Proyecto.Services.CompraService;
import com.elingenio.Proyecto.Services.OrderItemService;
import com.elingenio.Proyecto.Services.ProductoService;
import com.elingenio.Proyecto.Services.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/order-items")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private VentaService ventaService;

    @Autowired
    private CompraService compraService;

    @GetMapping
    public String listarOrderItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "idPedidoArticulo") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction,
            Model model) {
        // Create Pageable with sorting
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderItem> orderItemsPage = orderItemService.obtenerTodosPaginados(pageable);

        // Add pagination data
        model.addAttribute("orderItemsPage", orderItemsPage);
        // Add page size options
        model.addAttribute("pageSizes", Arrays.asList(5, 10, 20, 50));
        // Add sorting options for the view
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);

        return "vistas/order-items/list";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("orderItem", new OrderItem());
        model.addAttribute("productos", productoService.obtenerTodos());
        model.addAttribute("ventas", ventaService.obtenerTodas());
        return "vistas/order-items/form";
    }

    @PostMapping
    public String guardarOrderItem(@ModelAttribute("orderItem") OrderItem orderItem) {
        orderItemService.guardarOrderItem(orderItem);
        return "redirect:/order-items";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        OrderItem orderItem = orderItemService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("orderItem", orderItem);
        model.addAttribute("productos", productoService.obtenerTodos());
        model.addAttribute("ventas", ventaService.obtenerTodas());
        return "vistas/order-items/form";
    }

    @PostMapping("/{id}")
    public String actualizarOrderItem(@PathVariable Long id, @ModelAttribute("orderItem") OrderItem orderItem) {
        orderItem.setIdPedidoArticulo(id);
        orderItemService.guardarOrderItem(orderItem);
        return "redirect:/order-items";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarOrderItem(@PathVariable Long id) {
        orderItemService.eliminarOrderItem(id);
        return "redirect:/order-items";
    }
}