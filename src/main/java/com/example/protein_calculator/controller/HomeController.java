package com.example.protein_calculator.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "https://protein-calculator-front-end-7bi1.vercel.app")
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Protein Calculator Backend is Running 🚀";
    }
}
