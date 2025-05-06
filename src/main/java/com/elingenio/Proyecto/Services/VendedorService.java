package com.elingenio.Proyecto.Services;

import com.elingenio.Proyecto.Modelo.Vendedor;
import com.elingenio.Proyecto.Modelo.VendedorProducto;
import com.elingenio.Proyecto.Repository.VendedorRepository;
import com.elingenio.Proyecto.Repository.VendedorProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VendedorService {

    @Autowired
    private VendedorRepository vendedorRepository;

    @Autowired
    private VendedorProductoRepository vendedorProductoRepository;

    public Page<Vendedor> obtenerTodosPaginado(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return vendedorRepository.findAll(pageable);
    }

    public List<Vendedor> obtenerTodos() {
        return vendedorRepository.findAll();
    }

    public Optional<Vendedor> obtenerPorId(Long id) {
        return vendedorRepository.findById(id);
    }

    public Optional<Vendedor> findByCorreoElectronico(String correoElectronico) {
        return vendedorRepository.findByCorreoElectronico(correoElectronico);
    }

    public Vendedor guardarVendedor(Vendedor vendedor) {
        return vendedorRepository.save(vendedor);
    }

    public void eliminarVendedor(Long id) {
        vendedorRepository.deleteById(id);
    }

    public List<VendedorProducto> getProductosByVendedor(Long vendedorId) {
        return vendedorProductoRepository.findByVendedorIdVendedor(vendedorId);
    }
}