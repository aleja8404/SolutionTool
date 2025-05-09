package com.elingenio.Proyecto.Services;

import com.elingenio.Proyecto.Modelo.Cliente;
import com.elingenio.Proyecto.Repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Page<Cliente> obtenerTodosPaginados(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    public Page<Cliente> buscarPorNombreOCorreo(String search, Pageable pageable) {
        return clienteRepository.findByNombreContainingIgnoreCaseOrCorreoElectronicoContainingIgnoreCase(search, search, pageable);
    }

    public List<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> obtenerPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> obtenerPorCorreoElectronico(String correoElectronico) {
        return clienteRepository.findByCorreoElectronico(correoElectronico);
    }

    public Cliente guardarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public void eliminarCliente(Long id) {
        clienteRepository.deleteById(id);
    }
}