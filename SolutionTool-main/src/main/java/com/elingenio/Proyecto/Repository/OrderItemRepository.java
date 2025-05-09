package com.elingenio.Proyecto.Repository;

import com.elingenio.Proyecto.Modelo.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @EntityGraph(attributePaths = {"venta", "producto"})
    @Query("SELECT oi FROM OrderItem oi") // Explicit query to fetch all OrderItems
    List<OrderItem> findAllWithVentaAndProducto();
    List<OrderItem> findByVenta_Cliente_IdCliente(Long idCliente);
    List<OrderItem> findByVentaClienteIdCliente(Long clienteId);
    Page<OrderItem> findAll(Pageable pageable);
}