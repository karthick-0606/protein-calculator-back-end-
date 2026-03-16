package com.example.protein_calculator.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.example.protein_calculator.exception.ResourceNotFoundException;
import com.example.protein_calculator.model.ProteinUser;
import com.example.protein_calculator.service.ProteinService;

@RestController
@RequestMapping("/api/protein")
public class ProteinController {

    private final ProteinService service;

    public ProteinController(ProteinService service) {
        this.service = service;
    }

    @GetMapping
    public java.util.List<ProteinUser> getAll() {
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    public ProteinUser getById(@PathVariable Long id) {
        return service.getUserById(id);
    }

    @PostMapping
    public ProteinUser create(@RequestBody ProteinUser user) {
        return service.createUser(user);
    }

    @PutMapping("/{id}")
    public ProteinUser update(
            @PathVariable Long id,
            @RequestBody ProteinUser user) {
        try {
            return service.updateUser(id, user);
        } catch (ResourceNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteUser(id);
    }
}
