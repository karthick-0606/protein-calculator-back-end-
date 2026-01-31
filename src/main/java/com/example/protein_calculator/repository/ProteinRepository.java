package com.example.protein_calculator.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.protein_calculator.model.ProteinUser;

public interface ProteinRepository extends JpaRepository<ProteinUser, Long> {
}
