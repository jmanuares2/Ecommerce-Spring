package com.uade.tpo.e_commerce3.repository;

import com.uade.tpo.e_commerce3.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}