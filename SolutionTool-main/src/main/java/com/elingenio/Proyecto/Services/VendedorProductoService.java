package com.elingenio.Proyecto.Services;

import com.elingenio.Proyecto.Modelo.VendedorProducto;
import com.elingenio.Proyecto.Repository.VendedorProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VendedorProductoService {

    @Autowired
    private VendedorProductoRepository vendedorProductoRepository;

    public Page<VendedorProducto> obtenerTodosPaginado(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return vendedorProductoRepository.findAll(pageable);
    }

    public List<VendedorProducto> obtenerTodos() {
        return vendedorProductoRepository.findAll();
    }

    public VendedorProducto guardarVendedorProducto(VendedorProducto vendedorProducto) {
        return vendedorProductoRepository.save(vendedorProducto);
    }

    public void eliminarVendedorProducto(Long id) {
        vendedorProductoRepository.deleteById(id);
    }

    public Optional<VendedorProducto> obtenerPorId(Long id) {
        return vendedorProductoRepository.findById(id);
    }

    public VendedorProducto actualizarVendedorProducto(Long id, VendedorProducto vendedorProductoActualizado) {
        return vendedorProductoRepository.findById(id).map(vendedorProducto -> {
            vendedorProducto.setVendedor(vendedorProductoActualizado.getVendedor());
            vendedorProducto.setProducto(vendedorProductoActualizado.getProducto());
            vendedorProducto.setCantidad(vendedorProductoActualizado.getCantidad());
            vendedorProducto.setPrecio(vendedorProductoActualizado.getPrecio());
            return vendedorProductoRepository.save(vendedorProducto);
        }).orElseThrow(() -> new RuntimeException("VendedorProducto no encontrado con ID: " + id));
    }

    public List<VendedorProducto> findByVendedorId(Long vendedorId) {
        return vendedorProductoRepository.findByVendedorIdVendedor(vendedorId);
    }
}