package com.capstone.adpl.config;

import com.capstone.adpl.model.Role;
import com.capstone.adpl.model.RoleName;
import com.capstone.adpl.model.User;
import com.capstone.adpl.repository.RoleRepository;
import com.capstone.adpl.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create roles if they don't exist
        createRoleIfNotExists(RoleName.ROLE_ADMIN);
        createRoleIfNotExists(RoleName.ROLE_TEACHER);
        createRoleIfNotExists(RoleName.ROLE_STUDENT);

        // Create default admin user if not exists
        if (!userRepository.existsByEmail("admin@learning.com")) {
            User admin = new User();
            admin.setEmail("admin@learning.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("System");
            admin.setLastName("Admin");
            admin.setEnabled(true);

            Set<Role> roles = new HashSet<>();
            roleRepository.findByName(RoleName.ROLE_ADMIN).ifPresent(roles::add);
            admin.setRoles(roles);

            userRepository.save(admin);
            System.out.println("Default admin user created: admin@learning.com / admin123");
        }

        // Create sample teacher
        if (!userRepository.existsByEmail("teacher@learning.com")) {
            User teacher = new User();
            teacher.setEmail("teacher@learning.com");
            teacher.setPassword(passwordEncoder.encode("teacher123"));
            teacher.setFirstName("John");
            teacher.setLastName("Smith");
            teacher.setEnabled(true);

            Set<Role> roles = new HashSet<>();
            roleRepository.findByName(RoleName.ROLE_TEACHER).ifPresent(roles::add);
            teacher.setRoles(roles);

            userRepository.save(teacher);
            System.out.println("Sample teacher created: teacher@learning.com / teacher123");
        }

        // Create sample student
        if (!userRepository.existsByEmail("student@learning.com")) {
            User student = new User();
            student.setEmail("student@learning.com");
            student.setPassword(passwordEncoder.encode("student123"));
            student.setFirstName("Jane");
            student.setLastName("Doe");
            student.setEnabled(true);

            Set<Role> roles = new HashSet<>();
            roleRepository.findByName(RoleName.ROLE_STUDENT).ifPresent(roles::add);
            student.setRoles(roles);

            userRepository.save(student);
            System.out.println("Sample student created: student@learning.com / student123");
        }
    }

    private void createRoleIfNotExists(RoleName roleName) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = new Role(roleName);
            roleRepository.save(role);
            System.out.println("Created role: " + roleName);
        }
    }
}
