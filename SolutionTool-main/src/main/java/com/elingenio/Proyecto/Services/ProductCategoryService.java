package com.elingenio.Proyecto.Services;

import com.elingenio.Proyecto.Modelo.ProductCategory;
import com.elingenio.Proyecto.Repository.ProductCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductCategoryService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    // Método para obtener todas las categorías de productos
    public List<ProductCategory> findAll() {
        return productCategoryRepository.findAll();
    }

    // Método para obtener una categoría de producto por ID
    public Optional<ProductCategory> findById(Long id) {
        return productCategoryRepository.findById(id);
    }

    // Método para guardar o actualizar una categoría de producto
    public ProductCategory save(ProductCategory productCategory) {
        return productCategoryRepository.save(productCategory);
    }

    // Método para eliminar una categoría de producto por ID
    public void deleteById(Long id) {
        productCategoryRepository.deleteById(id);
    }
}
