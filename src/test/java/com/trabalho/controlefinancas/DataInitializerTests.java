package com.trabalho.controlefinancas.initializer;

import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.model.UserRole;
import com.trabalho.controlefinancas.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;

class DataInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void run_AdminUserDoesNotExist_CreatesAdminUser() throws Exception {
        // Arrange
        when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("admin123")).thenReturn("encodedPassword");

        // Act
        dataInitializer.run(null);

        // Assert
        verify(userRepository, times(1)).findByUsername("admin");
        verify(passwordEncoder, times(1)).encode("admin123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void run_AdminUserAlreadyExists_DoesNotCreateAdminUser() throws Exception {
        // Arrange
        User existingAdmin = new User();
        existingAdmin.setUsername("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(existingAdmin));

        // Act
        dataInitializer.run(null);

        // Assert
        verify(userRepository, times(1)).findByUsername("admin");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
