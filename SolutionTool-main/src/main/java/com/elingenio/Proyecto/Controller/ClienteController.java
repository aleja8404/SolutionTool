package com.elingenio.Proyecto.Controller;

import com.elingenio.Proyecto.Modelo.Cliente;
import com.elingenio.Proyecto.Services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public String listarClientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Cliente> clientes;
        if (search != null && !search.isEmpty()) {
            clientes = clienteService.buscarPorNombreOCorreo(search, pageable);
        } else {
            clientes = clienteService.obtenerTodosPaginados(pageable);
        }
        model.addAttribute("clientes", clientes.getContent());
        model.addAttribute("clientesPage", clientes);
        model.addAttribute("search", search);
        return "vistas/clientes/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoCliente(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "vistas/clientes/formulario";
    }

    @PostMapping
    public String guardarCliente(@ModelAttribute Cliente cliente) {
        clienteService.guardarCliente(cliente);
        return "redirect:/clientes";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarCliente(@PathVariable Long id, Model model) {
        Optional<Cliente> cliente = clienteService.obtenerPorId(id);
        if (cliente.isPresent()) {
            model.addAttribute("cliente", cliente.get());
            return "vistas/clientes/formulario";
        } else {
            return "redirect:/clientes";
        }
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarCliente(@PathVariable Long id, @ModelAttribute Cliente cliente) {
        cliente.setIdCliente(id);
        clienteService.guardarCliente(cliente);
        return "redirect:/clientes";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return "redirect:/clientes";
    }
}