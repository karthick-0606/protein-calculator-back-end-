package com.example.protein_calculator.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.protein_calculator.exception.ResourceNotFoundException;
import com.example.protein_calculator.model.ProteinUser;
import com.example.protein_calculator.repository.ProteinRepository;

@Service
public class ProteinService {

    private final ProteinRepository repository;

    public ProteinService(ProteinRepository repository) {
        this.repository = repository;
    }

    // CREATE
    public ProteinUser createUser(ProteinUser user) {
        user.setProteinRequired(calculateProtein(user.getWeight(), user.getGoal()));
        return repository.save(user);
    }

    // PUT UPDATE
    public ProteinUser updateUser(Long id, ProteinUser updated) {
        if (updated == null) {
            throw new IllegalArgumentException("Request body is required");
        }

        if (updated.getWeight() <= 0) {
            throw new IllegalArgumentException("weight must be greater than 0");
        }
        if (updated.getGoal() == null || updated.getGoal().isBlank()) {
            throw new IllegalArgumentException("goal is required");
        }
        if (updated.getAge() < 0) {
            throw new IllegalArgumentException("age cannot be negative");
        }
        if (updated.getHeight() < 0) {
            throw new IllegalArgumentException("height cannot be negative");
        }

        ProteinUser existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Update valid fields from request
        if (updated.getName() != null && !updated.getName().isBlank()) {
            existing.setName(updated.getName().trim());
        }
        if (updated.getAge() > 0) {
            existing.setAge(updated.getAge());
        }
        existing.setWeight(updated.getWeight());
        if (updated.getHeight() > 0) {
            existing.setHeight(updated.getHeight());
        }
        existing.setGoal(updated.getGoal().trim());

        // Recalculate protein safely and save
        double protein = calculateProtein(existing.getWeight(), existing.getGoal());
        existing.setProteinRequired(protein);

        return repository.save(existing);
    }

    // GET ALL USERS
    public List<ProteinUser> getAllUsers() {
        return repository.findAll();
    }

    // GET USER BY ID
    public ProteinUser getUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    // DELETE USER
    public void deleteUser(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        repository.deleteById(id);
    }

    // PROTEIN CALCULATION
    private double calculateProtein(double weight, String goal) {
        if (weight <= 0) {
            throw new IllegalArgumentException("weight must be greater than 0");
        }
        if (goal == null || goal.isBlank()) {
            throw new IllegalArgumentException("goal is required");
        }

        return switch (goal.trim().toLowerCase()) {
            case "bulking" -> weight * 2.2;
            case "cutting" -> weight * 1.8;
            default -> weight * 1.6;
        };
    }
}
