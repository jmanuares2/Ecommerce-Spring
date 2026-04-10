package com.uade.tpo.e_commerce3.controller;

import com.uade.tpo.e_commerce3.model.Cliente;
import com.uade.tpo.e_commerce3.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // Consultar todos
    @GetMapping
    public List<Cliente> getAll() {
        return clienteService.getAllClientes();
    }

    // Consultar por ID - Aquí estaba el error de nombre
    @GetMapping("/{id}")
    public Cliente getById(@PathVariable Long id) {
        return clienteService.getClienteById(id); 
    }

    // Crear
    @PostMapping
    public Cliente create(@RequestBody Cliente cliente) {
        return clienteService.saveCliente(cliente);
    }

    // Modificar / Update
    @PutMapping("/{id}")
    public Cliente update(@PathVariable Long id, @RequestBody Cliente cliente) {
        return clienteService.updateCliente(id, cliente);
    }

    // Eliminar
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        clienteService.deleteCliente(id);
    }
}