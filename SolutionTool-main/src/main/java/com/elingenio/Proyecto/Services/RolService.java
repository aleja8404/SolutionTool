package com.elingenio.Proyecto.Services;

import com.elingenio.Proyecto.Modelo.Rol;
import com.elingenio.Proyecto.Repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public Optional<Rol> findByNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }
}