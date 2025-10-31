package com.mss301.identity_service.config;

import com.mss301.identity_service.entity.Role;
import com.mss301.identity_service.entity.Status;
import com.mss301.identity_service.entity.User;
import com.mss301.identity_service.repository.RoleRepository;
import com.mss301.identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInit implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializeData();
    }

    private void initializeData() {
        String adminEmail = "admin@gmail.com";
        String teacherEmail = "teacher@gmail.com";
        String studentEmail = "student@gmail.com";

        Role teacherRole = roleRepository.findByCode("TEACHER")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setCode("TEACHER");
                    newRole.setName("Teacher");
                    return roleRepository.save(newRole);
                });
        Role adminRole = roleRepository.findByCode("ADMIN")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setCode("ADMIN");
                    newRole.setName("admin");
                    return roleRepository.save(newRole);
                });
        Role studentRole = roleRepository.findByCode("STUDENT")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setCode("STUDENT");
                    newRole.setName("Student");
                    return roleRepository.save(newRole);
                });

        if (!userRepository.existsByEmail(adminEmail) && !userRepository.existsByEmail(teacherEmail) && !userRepository.existsByEmail(studentEmail)) {

            User adminUser = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode("123"))
                    .role(adminRole)
                    .status(Status.ACTIVE)
                    .build();
            User teacherUser = User.builder()
                    .email(teacherEmail)
                    .password(passwordEncoder.encode("123"))
                    .role(teacherRole)
                    .status(Status.ACTIVE)
                    .build();
            User studentUser = User.builder()
                    .email(studentEmail)
                    .password(passwordEncoder.encode("123"))
                    .role(studentRole)
                    .status(Status.ACTIVE)
                    .build();
            userRepository.save(studentUser);
            userRepository.save(adminUser);
            userRepository.save(teacherUser);

            log.info("Admin user created successfully with email: {}", adminEmail);
        } else {
            log.info("Admin user already exists with email: {}", adminEmail);
        }


    }
}
