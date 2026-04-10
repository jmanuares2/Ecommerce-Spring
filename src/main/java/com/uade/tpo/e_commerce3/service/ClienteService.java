package com.uade.tpo.e_commerce3.service;

import com.uade.tpo.e_commerce3.model.Cliente;
import com.uade.tpo.e_commerce3.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> getAllClientes() {
        return clienteRepository.findAll();
    }

    public Cliente getClienteById(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    public Cliente saveCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public void deleteCliente(Long id) {
        clienteRepository.deleteById(id);
    }

    public Cliente updateCliente(Long id, Cliente clienteDetails) {
        Cliente cliente = getClienteById(id);
        if (cliente != null) {
            cliente.setNombre(clienteDetails.getNombre());
            cliente.setEmail(clienteDetails.getEmail());
            return clienteRepository.save(cliente);
        }
        return null;
    }
}