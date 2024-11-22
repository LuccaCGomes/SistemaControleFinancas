package com.trabalho.controlefinancas;

import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.model.UserRole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserModelTests {

    private User user;


    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testSetAndGetRole() {
        user.setRole(UserRole.ROLE_ADMIN);
        assertEquals(UserRole.ROLE_ADMIN, user.getRole());

        user.setRole(UserRole.ROLE_USER);
        assertEquals(UserRole.ROLE_USER, user.getRole());
    }

    @Test
    void testSetAndIsEnabled() {
        user.setEnabled(true);
        assertTrue(user.isEnabled());

        user.setEnabled(false);
        assertFalse(user.isEnabled());
    }
}
