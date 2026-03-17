package com.example.protein_calculator.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.protein_calculator.exception.ResourceNotFoundException;
import com.example.protein_calculator.model.ProteinUser;
import com.example.protein_calculator.repository.ProteinRepository;

@ExtendWith(MockitoExtension.class)
class ProteinServiceTest {

    @Mock
    private ProteinRepository repository;

    @InjectMocks
    private ProteinService service;

    private ProteinUser existingUser;

    @BeforeEach
    void setup() {
        existingUser = new ProteinUser();
        existingUser.setId(1L);
        existingUser.setName("John");
        existingUser.setAge(25);
        existingUser.setWeight(80.0);
        existingUser.setHeight(175.0);
        existingUser.setGoal("cutting");
    }

    @Test
    void createUser_setsProteinAndSaves() {
        ProteinUser input = new ProteinUser();
        input.setName("Jane");
        input.setAge(20);
        input.setWeight(70.0);
        input.setHeight(170.0);
        input.setGoal("bulking");

        when(repository.save(any(ProteinUser.class))).thenAnswer(inv -> inv.getArgument(0));

        ProteinUser saved = service.createUser(input);

        assertEquals(70.0 * 2.2, saved.getProteinRequired(), 0.0001);
        verify(repository).save(any(ProteinUser.class));
    }

    @Test
    void updateUser_updatesFieldsAndRecalculatesProtein() {
        ProteinUser update = new ProteinUser();
        update.setName("John Updated");
        update.setAge(26);
        update.setWeight(90.0);
        update.setHeight(180.0);
        update.setGoal("maintenance");

        when(repository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(repository.save(any(ProteinUser.class))).thenAnswer(inv -> inv.getArgument(0));

        ProteinUser result = service.updateUser(1L, update);

        assertEquals("John Updated", result.getName());
        assertEquals(26, result.getAge());
        assertEquals(90.0, result.getWeight());
        assertEquals(180.0, result.getHeight());
        assertEquals("maintenance", result.getGoal());
        assertEquals(90.0 * 1.6, result.getProteinRequired(), 0.0001);
    }

    @Test
    void updateUser_nullBody_throwsIllegalArgument() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateUser(1L, null));
        assertTrue(ex.getMessage().toLowerCase().contains("request body"));
    }

    @Test
    void updateUser_invalidWeight_throwsIllegalArgument() {
        ProteinUser update = new ProteinUser();
        update.setWeight(0.0);
        update.setGoal("bulking");
        update.setAge(20);
        update.setHeight(170.0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateUser(1L, update));
        assertTrue(ex.getMessage().toLowerCase().contains("weight"));
    }

    @Test
    void updateUser_invalidGoal_throwsIllegalArgument() {
        ProteinUser update = new ProteinUser();
        update.setWeight(70.0);
        update.setGoal(" ");
        update.setAge(20);
        update.setHeight(170.0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateUser(1L, update));
        assertTrue(ex.getMessage().toLowerCase().contains("goal"));
    }

    @Test
    void updateUser_negativeAgeOrHeight_throwIllegalArgument() {
        ProteinUser updateAge = new ProteinUser();
        updateAge.setWeight(70.0);
        updateAge.setGoal("bulking");
        updateAge.setAge(-1);
        updateAge.setHeight(170.0);

        assertThrows(IllegalArgumentException.class, () -> service.updateUser(1L, updateAge));

        ProteinUser updateHeight = new ProteinUser();
        updateHeight.setWeight(70.0);
        updateHeight.setGoal("bulking");
        updateHeight.setAge(20);
        updateHeight.setHeight(-10.0);

        assertThrows(IllegalArgumentException.class, () -> service.updateUser(1L, updateHeight));
    }

    @Test
    void updateUser_notFound_throwsResourceNotFound() {
        ProteinUser update = new ProteinUser();
        update.setWeight(70.0);
        update.setGoal("bulking");
        update.setAge(20);
        update.setHeight(170.0);

        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateUser(99L, update));
    }

    @Test
    void getAllUsers_returnsListFromRepository() {
        when(repository.findAll()).thenReturn(List.of(existingUser));

        List<ProteinUser> users = service.getAllUsers();

        assertEquals(1, users.size());
        verify(repository).findAll();
    }

    @Test
    void getUserById_found() {
        when(repository.findById(1L)).thenReturn(Optional.of(existingUser));

        ProteinUser result = service.getUserById(1L);

        assertEquals(existingUser, result);
    }

    @Test
    void getUserById_notFound_throwsResourceNotFound() {
        when(repository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getUserById(2L));
    }

    @Test
    void deleteUser_existing_deletes() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deleteUser(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deleteUser_missing_throwsResourceNotFound() {
        when(repository.existsById(2L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.deleteUser(2L));
    }

    @Test
    void calculateProtein_withNonPositiveWeight_throwsIllegalArgument() throws Exception {
        Method method = ProteinService.class.getDeclaredMethod("calculateProtein", double.class, String.class);
        method.setAccessible(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            try {
                method.invoke(service, 0.0, "bulking");
            } catch (InvocationTargetException e) {
                throw (RuntimeException) e.getCause();
            }
        });

        assertTrue(ex.getMessage().toLowerCase().contains("weight"));
    }

    @Test
    void calculateProtein_withNullOrBlankGoal_throwsIllegalArgument() throws Exception {
        Method method = ProteinService.class.getDeclaredMethod("calculateProtein", double.class, String.class);
        method.setAccessible(true);

        IllegalArgumentException exNull = assertThrows(IllegalArgumentException.class, () -> {
            try {
                method.invoke(service, 70.0, (Object) null);
            } catch (InvocationTargetException e) {
                throw (RuntimeException) e.getCause();
            }
        });

        IllegalArgumentException exBlank = assertThrows(IllegalArgumentException.class, () -> {
            try {
                method.invoke(service, 70.0, " ");
            } catch (InvocationTargetException e) {
                throw (RuntimeException) e.getCause();
            }
        });

        assertTrue(exNull.getMessage().toLowerCase().contains("goal"));
        assertTrue(exBlank.getMessage().toLowerCase().contains("goal"));
    }
}
