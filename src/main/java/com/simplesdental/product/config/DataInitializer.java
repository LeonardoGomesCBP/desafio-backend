package com.simplesdental.product.config;

import com.simplesdental.product.model.User;
import com.simplesdental.product.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        logger.info("Inicializando dados...");
        initAdmin();
    }

    private void initAdmin() {
        try {
            String adminEmail = "contato@simplesdental.com";

            Optional<User> existingAdmin = userRepository.findByEmail(adminEmail);

            if (!existingAdmin.isPresent()) {
                logger.info("Criando usuário admin inicial");

                User admin = User.builder()
                        .name("Admin")
                        .email(adminEmail)
                        .password(passwordEncoder.encode("KMbT%5wT*R!46i@@YHqx"))
                        .role("admin")
                        .build();

                userRepository.save(admin);
                logger.info("Usuário admin criado com sucesso");
            } else {
                logger.info("Usuário admin já existe");
            }
        } catch (Exception e) {
            logger.error("Erro ao inicializar usuário admin: {}", e.getMessage(), e);
        }
    }
}
