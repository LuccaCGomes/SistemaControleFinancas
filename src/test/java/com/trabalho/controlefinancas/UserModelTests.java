package com.trabalho.controlefinancas.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void getAuthorities_ShouldReturnCorrectRole() {
        // Arrange
        User user = new User();
        user.setRole(UserRole.ROLE_ADMIN);

        // Act
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // Assert
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void isAccountNonExpired_ShouldAlwaysReturnTrue() {
        // Arrange
        User user = new User();

        // Act & Assert
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked_ShouldAlwaysReturnTrue() {
        // Arrange
        User user = new User();

        // Act & Assert
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired_ShouldAlwaysReturnTrue() {
        // Arrange
        User user = new User();

        // Act & Assert
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void isEnabled_ShouldReflectEnabledProperty() {
        // Arrange
        User user = new User();
        user.setEnabled(false);

        // Act & Assert
        assertFalse(user.isEnabled());
        user.setEnabled(true);
        assertTrue(user.isEnabled());
    }
}
