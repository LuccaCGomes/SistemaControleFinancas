package com.trabalho.controlefinancas.initializer;

import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.model.UserRole;
import com.trabalho.controlefinancas.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Verificar se o usuário admin já existe
        if (userRepository.findByUsername("admin").isEmpty()) {
            // Criar o usuário admin
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123")); // Define a senha
            adminUser.setRole(UserRole.ROLE_ADMIN);
            adminUser.setEnabled(true);

            userRepository.save(adminUser);
            System.out.println("Usuário admin criado com sucesso!");
        } else {
            System.out.println("Usuário admin já existe.");
        }
    }
}



