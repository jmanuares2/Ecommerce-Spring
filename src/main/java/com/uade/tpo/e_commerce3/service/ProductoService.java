package com.uade.tpo.e_commerce3.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.e_commerce3.model.Producto;
import com.uade.tpo.e_commerce3.repository.ProductoRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional //le dice a springboot que este servicio es transaccional. Si alguna operación falla, la transacción se revertirá automáticamente, sin afectar la base de datos.
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;
    
    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    public Producto getProductoById(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    public void deleteProductoById(Long id) {
        productoRepository.deleteById(id);
    }

    public Producto saveProducto(Producto producto) {
        return productoRepository.save(producto);

    }

    public Producto updateProducto(Long id, Producto producto) {
        Producto existingProducto = getProductoById(id);
        if (existingProducto != null) {
            existingProducto.setNombre(producto.getNombre());
            existingProducto.setDescripcion(producto.getDescripcion());
            return productoRepository.save(existingProducto);
        }
        return null;
    }
}
