package com.uade.tpo.e_commerce3.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.tpo.e_commerce3.model.Producto;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    //findAll() ya está implementado por JpaRepository, no es necesario definirlo aquí
    // select * from productos

    //save, delete, findById, findAll, update etc. también están implementados por JpaRepository

    //query metods personalizados pueden ser definidos aquí, por ejemplo:
    //findByNombre(String nombre); 
    // la instrucción SQL sería: select * from productos where nombre like '%nombre%'
    List<Producto> findByNombreContaining(String Nombre); //containing es para buscar coincidencias parciales

    //sql sería: select * from productos where precio < precio
    List<Producto> findByPrecioLessThan(Double precio);

    //findByPrecioBetween(Double minPrecio, Double maxPrecio);
    List<Producto> findByPrecioBetween(Double minPrecio, Double maxPrecio);
}
