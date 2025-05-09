package com.elingenio.Proyecto.config;

import com.elingenio.Proyecto.Modelo.Cliente;
import com.elingenio.Proyecto.Modelo.Rol;
import com.elingenio.Proyecto.Modelo.Usuario;
import com.elingenio.Proyecto.Repository.ClienteRepository;
import com.elingenio.Proyecto.Repository.RolRepository;
import com.elingenio.Proyecto.Repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Configuration
public class DataInitializer {

    @Bean
    @Transactional
    public CommandLineRunner initDefaultUsers(UsuarioRepository usuarioRepository, RolRepository rolRepository, ClienteRepository clienteRepository) {
        return args -> {
            // Crear roles si no existen
            initializeRoles(rolRepository);

            // Crear usuarios y clientes con sus roles
            createUserAndClienteIfNotExists(usuarioRepository, rolRepository, clienteRepository,
                    "admin@example.com", "admin123", "Admin", "User", "ROLE_ADMINISTRADOR");
            createUserAndClienteIfNotExists(usuarioRepository, rolRepository, clienteRepository,
                    "proveedor@example.com", "prov123", "Proveedor", "User", "ROLE_PROVEEDOR");
            createUserAndClienteIfNotExists(usuarioRepository, rolRepository, clienteRepository,
                    "cliente@example.com", "cli123", "Cliente", "User", "ROLE_CLIENTE");
            createUserAndClienteIfNotExists(usuarioRepository, rolRepository, clienteRepository,
                    "vendedor@example.com", "vend123", "eddy", "fransico", "ROLE_VENDEDOR");
        };
    }

    private void initializeRoles(RolRepository rolRepository) {
        String[] roles = {"ROLE_ADMINISTRADOR", "ROLE_VENDEDOR", "ROLE_CLIENTE", "ROLE_PROVEEDOR"};
        for (String roleName : roles) {
            if (rolRepository.findByNombre(roleName).isEmpty()) {
                Rol rol = new Rol();
                rol.setNombre(roleName);
                rolRepository.save(rol);
                System.out.println("✅ Rol creado con éxito: " + roleName);
            } else {
                System.out.println("⚠️ Rol ya existe: " + roleName);
            }
        }
    }

    private void createUserAndClienteIfNotExists(UsuarioRepository usuarioRepository, RolRepository rolRepository, ClienteRepository clienteRepository,
                                                 String email, String password, String nombre, String apellido, String roleName) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(email);
        if (usuarioExistente.isEmpty()) {
            // Crear Usuario
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setEmail(email);
            usuario.setPassword(new BCryptPasswordEncoder().encode(password));

            Optional<Rol> rol = rolRepository.findByNombre(roleName);
            if (rol.isPresent()) {
                usuario.setRoles(Collections.singleton(rol.get()));
            } else {
                throw new RuntimeException("Rol no encontrado: " + roleName);
            }
            usuarioRepository.save(usuario);
            System.out.println("✅ Usuario creado con éxito: " + email + " con rol " + roleName);
            
            // Crear Cliente (solo para ROLE_CLIENTE)
            if ("ROLE_CLIENTE".equals(roleName)) {
                Optional<Cliente> clienteExistente = clienteRepository.findByCorreoElectronico(email);
                if (clienteExistente.isEmpty()) {
                    Cliente cliente = new Cliente();
                    cliente.setNombre(nombre);
                    cliente.setCorreoElectronico(email);
                    cliente.setTelefono("123456789");
                    cliente.setDireccion("Dirección por defecto");
                    cliente.setCreadoEn(LocalDateTime.now());
                    cliente.setActualizadoEn(LocalDateTime.now());
                    clienteRepository.save(cliente);
                    System.out.println("✅ Cliente creado con éxito: " + email);
                } else {
                    System.out.println("⚠️ Cliente ya existe: " + email);
                }
            }
        } else {
            System.out.println("⚠️ El usuario ya existe, no es necesario crearlo: " + email);
        }
    }
}  
