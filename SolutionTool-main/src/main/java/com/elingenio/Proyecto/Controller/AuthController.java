package com.elingenio.Proyecto.Controller;

import com.elingenio.Proyecto.Modelo.Cliente;
import com.elingenio.Proyecto.Modelo.Rol;
import com.elingenio.Proyecto.Modelo.Usuario;
import com.elingenio.Proyecto.Services.ClienteService;
import com.elingenio.Proyecto.Services.RolService;
import com.elingenio.Proyecto.Services.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Collections;


@Controller
public class AuthController {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private RolService rolService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String iniciarSesion() {
        return "login";
    }




@GetMapping("/ventana-home-producto")
public String buscarProductos() {
    return "ventana-home/ventana-productos";
}

    @GetMapping("/register")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register";
    }

    @PostMapping("/perform_register")
    public String registrarUsuario(@ModelAttribute("usuario") Usuario usuario, Model model) {
        // Check if email already exists
        if (usuarioServicio.obtenerPorEmail(usuario.getEmail()).isPresent()) {
            model.addAttribute("error", "El correo ya está registrado.");
            return "register";
        }

        // Set ROLE_CLIENTE
        Rol clienteRol = rolService.findByNombre("ROLE_CLIENTE")
                .orElseThrow(() -> new RuntimeException("Rol ROLE_CLIENTE no encontrado"));
        usuario.setRoles(Collections.singleton(clienteRol));

        // Encode password
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Save Usuario
        usuarioServicio.guardar(usuario);
        // Create and save corresponding Cliente
        Cliente cliente = new Cliente();
        cliente.setNombre(usuario.getNombre());
        cliente.setCorreoElectronico(usuario.getEmail());
        cliente.setCreadoEn(LocalDateTime.now());
        clienteService.guardarCliente(cliente);

        return "redirect:/login?registered";
    }
}