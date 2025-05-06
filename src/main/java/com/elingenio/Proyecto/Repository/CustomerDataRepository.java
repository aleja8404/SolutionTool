package com.elingenio.Proyecto.Repository;

import com.elingenio.Proyecto.Modelo.CustomerData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerDataRepository extends JpaRepository<CustomerData, Long> {

    @Query("SELECT c FROM CustomerData c WHERE " +
            "(:edadMin IS NULL OR c.edad >= :edadMin) AND " +
            "(:edadMax IS NULL OR c.edad <= :edadMax) AND " +
            "(:frecuenciaCompraMin IS NULL OR c.frecuenciaCompra >= :frecuenciaCompraMin) AND " +
            "(:frecuenciaCompraMax IS NULL OR c.frecuenciaCompra <= :frecuenciaCompraMax) AND " +
            "(:valorTotalCompraMin IS NULL OR c.valorTotalCompra >= :valorTotalCompraMin) AND " +
            "(:valorTotalCompraMax IS NULL OR c.valorTotalCompra <= :valorTotalCompraMax) AND " +
            "(:ultimaCompraMin IS NULL OR c.ultimaCompra >= :ultimaCompraMin) AND " +
            "(:ultimaCompraMax IS NULL OR c.ultimaCompra <= :ultimaCompraMax) AND " +
            "(:metodoPago IS NULL OR c.metodoPago = :metodoPago) AND " +
            "(:categoriaCliente IS NULL OR c.categoriaCliente = :categoriaCliente)")
    Page<CustomerData> findByFilters(
            @Param("edadMin") Integer edadMin,
            @Param("edadMax") Integer edadMax,
            @Param("frecuenciaCompraMin") Integer frecuenciaCompraMin,
            @Param("frecuenciaCompraMax") Integer frecuenciaCompraMax,
            @Param("valorTotalCompraMin") Double valorTotalCompraMin,
            @Param("valorTotalCompraMax") Double valorTotalCompraMax,
            @Param("ultimaCompraMin") Integer ultimaCompraMin,
            @Param("ultimaCompraMax") Integer ultimaCompraMax,
            @Param("metodoPago") String metodoPago,
            @Param("categoriaCliente") String categoriaCliente,
            Pageable pageable
    );

    @Query("SELECT DISTINCT c.metodoPago FROM CustomerData c")
    List<String> findDistinctMetodoPago();

    @Query("SELECT DISTINCT c.categoriaCliente FROM CustomerData c")
    List<String> findDistinctCategoriaCliente();
}