package com.example.protein_calculator.model;

import jakarta.persistence.*;

@Entity
public class ProteinUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int age;
    private double weight;
    private double height;
    private String goal;
    private double proteinRequired;

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public double getProteinRequired() { return proteinRequired; }
    public void setProteinRequired(double proteinRequired) {
        this.proteinRequired = proteinRequired;
    }
}
