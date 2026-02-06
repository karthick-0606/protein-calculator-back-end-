
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

    // CREATE USER
    public ProteinUser createUser(ProteinUser user) {

        if (user.getWeight() <= 0) {
            throw new RuntimeException("Weight must be greater than 0");
        }

        double protein = calculateProtein(user.getWeight(), user.getGoal());
        user.setProteinRequired(protein);

        return repository.save(user);
    }

    // UPDATE SELECTED FIELDS
    public ProteinUser updateSelectedFields(Long id, ProteinUser updated) {

        ProteinUser existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (updated.getName() != null) {
            existing.setName(updated.getName());
        }

        if (updated.getAge() > 0) {
            existing.setAge(updated.getAge());
        }

        if (updated.getHeight() > 0) {
            existing.setHeight(updated.getHeight());
        }

        if (updated.getWeight() > 0) {
            existing.setWeight(updated.getWeight());
        }

        if (updated.getGoal() != null) {
            existing.setGoal(updated.getGoal());
        }

        // recalculate protein if weight or goal changed
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
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // DELETE USER
    public void deleteUser(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        repository.deleteById(id);
    }

    // PROTEIN CALCULATION LOGIC
    private double calculateProtein(double weight, String goal) {

        if (weight <= 0 || goal == null) {
            return 0;
        }

        return switch (goal.toLowerCase()) {
            case "bulking" -> weight * 2.2;
            case "cutting" -> weight * 1.8;
            default -> weight * 1.6;
        };
    }
}
