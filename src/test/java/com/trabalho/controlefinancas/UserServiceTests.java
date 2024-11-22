package com.trabalho.controlefinancas;

import com.trabalho.controlefinancas.service.UserService;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registro_SalvaUserQuantoNaoExiste() {
        String username = "testeUser";
        String password = "testeSenha";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedSenha");

        userService.registerUser(username, password);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registro_LancaExcecaoQuandoUserExiste() {
        String username = "userExistente";
        String password = "senhaTeste";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User(username, password)));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(username, password);
        });

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findUserByUsername_RetornaUserQuandoExiste() {
        String username = "userExistente";
        User mockUser = new User(username, "senhaTeste");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        User result = userService.findUserByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userRepository, times(2)).findByUsername(username);
    }

    @Test
    void findUserByUsername_LancaExcecaoQuandoUserNaoExiste() {
        String username = "userInexistente";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.findUserByUsername(username);
        });

        assertEquals("Username doesn't exists", exception.getMessage());
    }

    @Test
    void loadUserByUsername_RetornaUserDetailsQuandoExiste() {
        String username = "userExistente";
        User mockUser = new User(username, "senhaTeste");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        UserDetails result = userService.loadUserByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    void loadUserByUsername_LancaExcecaoQuandoUserNaoExiste() {
        String username = "userInexistente";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(username);
        });

        assertEquals("Usuário de nome \"" + username + "\" não foi achado", exception.getMessage());
    }

    @Test
    void loginUser_RetornaTrueQuandoUserESenhaDaoMatch() {
        String username = "userValido";
        String password = "senhaTeste";
        User mockUser = new User(username, password);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        boolean result = userService.loginUser(username, password);

        assertTrue(result);
    }

    @Test
    void loginUser_RetornaFalsaQuandoSenhaErrada() {
        String username = "userValido";
        String password = "senhaErrada";
        User mockUser = new User(username, "senhaCorreta");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        boolean result = userService.loginUser(username, password);

        assertFalse(result);
    }

    @Test
    void loginUser_RetornaFalsoQuandoUserNaoExiste() {
        String username = "userInexistente";
        String password = "senhaTeste";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        boolean result = userService.loginUser(username, password);

        assertFalse(result);
    }
}