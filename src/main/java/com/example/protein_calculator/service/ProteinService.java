package com.example.protein_calculator.service;

import java.util.List;

import org.springframework.stereotype.Service;

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

    // PATCH UPDATE (selected fields)
    public ProteinUser updateSelectedFields(Long id, ProteinUser updated) {
        ProteinUser existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // update fields
        existing.setWeight(updated.getWeight());
        existing.setHeight(updated.getHeight());
        existing.setGoal(updated.getGoal());

        // recalculate protein
        double protein = calculateProtein(updated.getWeight(), updated.getGoal());
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
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // DELETE USER
    public void deleteUser(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        repository.deleteById(id);
    }

    // PROTEIN CALCULATION
    private double calculateProtein(double weight, String goal) {
        if (goal == null) return 0;

        return switch (goal.toLowerCase()) {
            case "bulking" -> weight * 2.2;
            case "cutting" -> weight * 1.8;
            default -> weight * 1.6;
        };
    }
}
