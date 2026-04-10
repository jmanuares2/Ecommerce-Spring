package com.uade.tpo.e_commerce3.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nombre;
    private String nacionalidad;
    private String biografía;

    @OneToMany(mappedBy = "autor", fetch = FetchType.LAZY)
    private List<Libro> libros;

}
