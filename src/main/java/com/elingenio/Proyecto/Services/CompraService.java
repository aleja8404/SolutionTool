package com.elingenio.Proyecto.Services;

import com.elingenio.Proyecto.Modelo.Compra;
import com.elingenio.Proyecto.Modelo.VendedorProducto;
import com.elingenio.Proyecto.Modelo.Venta;
import com.elingenio.Proyecto.Repository.CompraRepository;
import com.elingenio.Proyecto.Repository.VentaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;
    @Autowired
    private VentaRepository ventaRepository ;

    public List<Compra> obtenerTodas() {
        return compraRepository.findAll();
    }

    public Optional<Compra> obtenerPorId(Long id) {
        return compraRepository.findById(id);
    }
    // Obtener ventas por ID de vendedor (ya requerido por el controlador)
    public List<Venta> findByVendedorId(Long vendedorId) {
        return ventaRepository.findByVendedorIdVendedor(vendedorId);
    }
    public Compra registrarCompra(Compra compra) {
        return compraRepository.save(compra);
    }

    public void eliminarCompra(Long id) {
        compraRepository.deleteById(id);
    }
}
