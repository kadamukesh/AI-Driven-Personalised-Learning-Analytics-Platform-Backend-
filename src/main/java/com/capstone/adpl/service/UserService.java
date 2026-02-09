package com.capstone.adpl.service;

import com.capstone.adpl.dto.response.UserResponse;
import com.capstone.adpl.exception.BadRequestException;
import com.capstone.adpl.exception.ResourceNotFoundException;
import com.capstone.adpl.model.Role;
import com.capstone.adpl.model.RoleName;
import com.capstone.adpl.model.User;
import com.capstone.adpl.repository.RoleRepository;
import com.capstone.adpl.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return UserResponse.fromEntity(user);
    }

    public List<UserResponse> getUsersByRole(String roleName) {
        RoleName role = RoleName.valueOf("ROLE_" + roleName.toUpperCase());
        return userRepository.findByRoleName(role).stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse updateUser(Long id, UserResponse request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }

    @Transactional
    public UserResponse updateUserRole(Long id, String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        RoleName role = RoleName.valueOf("ROLE_" + roleName.toUpperCase());
        Role newRole = roleRepository.findByName(role)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));

        Set<Role> roles = new HashSet<>();
        roles.add(newRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }

    @Transactional
    public void toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }

    public List<UserResponse> searchUsers(String name) {
        return userRepository.searchByName(name).stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public long countByRole(String roleName) {
        RoleName role = RoleName.valueOf("ROLE_" + roleName.toUpperCase());
        return userRepository.countByRole(role);
    }
}
