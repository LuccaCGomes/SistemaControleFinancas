package com.trabalho.controlefinancas.service;

import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void registerUser(String username, String password) {
        User user = new User(username, password);
        userRepository.save(user);
    }

    public boolean loginUser(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent() && user.get().getPassword().equals(password);
    }
}