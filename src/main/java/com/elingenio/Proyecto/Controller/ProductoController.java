package com.elingenio.Proyecto.Controller;

import com.elingenio.Proyecto.Modelo.Producto;
import com.elingenio.Proyecto.Services.ProductoService;
import com.elingenio.Proyecto.Services.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProveedorService proveedorService;

    @Value("${app.upload.dir:uploads}") // Valor por defecto: carpeta 'uploads'
    private String uploadDir;

    @GetMapping
    public String listarProductos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "idProducto") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction,
            Model model) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Producto> productosPage = productoService.obtenerTodosPaginado(pageable);

        model.addAttribute("productosPage", productosPage);
        model.addAttribute("pageSizes", Arrays.asList(5, 10, 20, 50));
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);

        return "vistas/productos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoProducto(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("proveedores", proveedorService.obtenerTodos());
        return "vistas/productos/formulario";
    }

    @PostMapping
    public String guardarProducto(@ModelAttribute Producto producto,
                                  @RequestParam("fotoFile") MultipartFile fotoFile) throws IOException {
        if (!fotoFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + fotoFile.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir + "/" + fileName); // Usa la carpeta externa
            Files.createDirectories(uploadPath.getParent());
            Files.write(uploadPath, fotoFile.getBytes());
            producto.setFoto("/uploads/" + fileName);
        }
        productoService.guardarProducto(producto);
        return "redirect:/productos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarProducto(@PathVariable Long id, Model model) {
        Optional<Producto> producto = productoService.obtenerPorId(id);
        if (producto.isPresent()) {
            model.addAttribute("producto", producto.get());
            model.addAttribute("proveedores", proveedorService.obtenerTodos());
            return "vistas/productos/formulario";
        } else {
            return "redirect:/productos";
        }
    }

    @PostMapping("/editar/{id}")
    public String actualizarProducto(@PathVariable Long id,
                                     @ModelAttribute Producto producto,
                                     @RequestParam("fotoFile") MultipartFile fotoFile) throws IOException {
        producto.setIdProducto(id);
        if (!fotoFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + fotoFile.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir + "/" + fileName); // Usa la carpeta externa
            Files.createDirectories(uploadPath.getParent());
            Files.write(uploadPath, fotoFile.getBytes());
            producto.setFoto("/uploads/" + fileName);
        } else {
            // Retain existing photo if no new file is uploaded
            productoService.obtenerPorId(id).ifPresent(existing -> {
                if (existing.getFoto() != null) {
                    producto.setFoto(existing.getFoto());
                }
            });
        }
        productoService.guardarProducto(producto);
        return "redirect:/productos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return "redirect:/productos";
    }
}