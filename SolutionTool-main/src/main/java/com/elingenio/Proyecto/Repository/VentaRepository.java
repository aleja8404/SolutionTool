package com.elingenio.Proyecto.Repository;

import com.elingenio.Proyecto.Modelo.OrderItem;
import com.elingenio.Proyecto.Modelo.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByVendedorIdVendedor(Long idVendedor);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.venta.idVenta = :ventaId")
    List<OrderItem> findOrderItemsByVentaId(@Param("ventaId") Long ventaId);

    long countByEstado(String estado);

    long countByDeviceType(String deviceType);

    // Query to sum total for COMPLETADA sales by month and year
    @Query("SELECT MONTH(v.fechaDelPedido) as month, COALESCE(SUM(v.total), 0) as total " +
            "FROM Venta v WHERE YEAR(v.fechaDelPedido) = :year AND v.estado = 'COMPLETADA' " +
            "GROUP BY MONTH(v.fechaDelPedido) ORDER BY MONTH(v.fechaDelPedido)")
    List<Object[]> findMonthlySalesByYear(@Param("year") int year);

    // Fixed method with explicit query to count COMPLETADA sales
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.estado = 'COMPLETADA'")
    long countByEstadoCompletada();
}