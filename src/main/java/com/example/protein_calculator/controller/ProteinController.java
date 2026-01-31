package com.example.protein_calculator.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.protein_calculator.model.ProteinUser;
import com.example.protein_calculator.service.ProteinService;

@RestController
@RequestMapping("/api/protein")
@CrossOrigin(origins = "*")
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

    @PatchMapping("/{id}")
    public ProteinUser updateFields(
            @PathVariable Long id,
            @RequestBody ProteinUser user) {
        return service.updateSelectedFields(id, user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteUser(id);
    }
}
