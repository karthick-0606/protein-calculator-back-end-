package com.example.protein_calculator.controller;

import java.util.List;

import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.protein_calculator.exception.ResourceNotFoundException;
import com.example.protein_calculator.model.ProteinUser;
import com.example.protein_calculator.service.ProteinService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = { ProteinController.class, HomeController.class })
class ProteinControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProteinService service;

    @Autowired
    private ObjectMapper objectMapper;

    private ProteinUser sampleUser() {
        ProteinUser user = new ProteinUser();
        user.setId(1L);
        user.setName("John");
        user.setAge(25);
        user.setWeight(80.0);
        user.setHeight(175.0);
        user.setGoal("cutting");
        user.setProteinRequired(144.0);
        return user;
    }

    @Test
    void getAll_returnsList() throws Exception {
        given(service.getAllUsers()).willReturn(List.of(sampleUser()));

        mockMvc.perform(get("/api/protein"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("John")));
    }

    @Test
    void getById_returnsUser() throws Exception {
        given(service.getUserById(1L)).willReturn(sampleUser());

        mockMvc.perform(get("/api/protein/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void createUser_createsAndReturns() throws Exception {
        ProteinUser user = sampleUser();
        given(service.createUser(any(ProteinUser.class))).willReturn(user);

        mockMvc.perform(post("/api/protein")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John")));
    }

    @Test
    void updateUser_success() throws Exception {
        ProteinUser user = sampleUser();
        given(service.updateUser(eq(1L), any(ProteinUser.class))).willReturn(user);

        mockMvc.perform(put("/api/protein/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void updateUser_notFound_mapsTo404() throws Exception {
        ProteinUser user = sampleUser();
        given(service.updateUser(eq(99L), any(ProteinUser.class)))
                .willThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(put("/api/protein/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_illegalArgument_mapsTo400() throws Exception {
        ProteinUser user = sampleUser();
        given(service.updateUser(eq(1L), any(ProteinUser.class)))
                .willThrow(new IllegalArgumentException("Bad request"));

        mockMvc.perform(put("/api/protein/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser_deletes() throws Exception {
        doNothing().when(service).deleteUser(1L);

        mockMvc.perform(delete("/api/protein/1"))
                .andExpect(status().isOk());
    }

    @Test
    void homeEndpoint_returnsMessage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("Protein Calculator Backend is Running"));
    }
}
