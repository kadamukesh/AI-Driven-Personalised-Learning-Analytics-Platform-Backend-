package com.capstone.adpl.service;

import com.capstone.adpl.dto.request.GoogleLoginRequest;
import com.capstone.adpl.dto.request.LoginRequest;
import com.capstone.adpl.dto.request.RegisterRequest;
import com.capstone.adpl.dto.response.AuthResponse;
import com.capstone.adpl.dto.response.UserResponse;
import com.capstone.adpl.exception.BadRequestException;
import com.capstone.adpl.exception.ResourceNotFoundException;
import com.capstone.adpl.model.Role;
import com.capstone.adpl.model.RoleName;
import com.capstone.adpl.model.User;
import com.capstone.adpl.repository.RoleRepository;
import com.capstone.adpl.repository.UserRepository;
import com.capstone.adpl.security.JwtTokenProvider;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Value("${google.client-id}")
    private String googleClientId;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    // ===================== NORMAL LOGIN =====================
    public AuthResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", "email", request.getEmail())
                );

        return new AuthResponse(
                accessToken,
                refreshToken,
                tokenProvider.getJwtExpiration(),
                UserResponse.fromEntity(user)
        );
    }

    // ===================== GOOGLE LOGIN =====================
    @Transactional
    public AuthResponse googleLogin(GoogleLoginRequest request) {

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory()
            )
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getToken());

            if (idToken == null) {
                throw new BadRequestException("Invalid Google token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String firstName = (String) payload.get("given_name");
            String lastName = (String) payload.get("family_name");

            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> createGoogleUser(email, firstName, lastName));

            String accessToken = tokenProvider.generateToken(email);
            String refreshToken = tokenProvider.generateRefreshToken(email);

            return new AuthResponse(
                    accessToken,
                    refreshToken,
                    tokenProvider.getJwtExpiration(),
                    UserResponse.fromEntity(user)
            );

        } catch (Exception e) {
            throw new BadRequestException("Google authentication failed: " + e.getMessage());
        }
    }

    private User createGoogleUser(String email, String firstName, String lastName) {

        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(passwordEncoder.encode("google-auth-" + System.currentTimeMillis()));
        user.setEnabled(true);

        Role studentRole = roleRepository.findByName(RoleName.ROLE_STUDENT)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role", "name", "ROLE_STUDENT")
                );

        Set<Role> roles = new HashSet<>();
        roles.add(studentRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    // ===================== REGISTER =====================
    @Transactional
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);

        RoleName roleName = RoleName.ROLE_STUDENT;

        if (request.getRole() != null && !request.getRole().isEmpty()) {
            try {
                roleName = RoleName.valueOf("ROLE_" + request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid role: " + request.getRole());
            }
        }

        final RoleName finalRoleName = roleName;
        Role role = roleRepository.findByName(finalRoleName)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role", "name", finalRoleName.name())
                );

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }

    // ===================== REFRESH TOKEN =====================
    public AuthResponse refreshToken(String refreshToken) {

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }

        String email = tokenProvider.getUsernameFromToken(refreshToken);

        String newAccessToken = tokenProvider.generateToken(email);
        String newRefreshToken = tokenProvider.generateRefreshToken(email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", "email", email)
                );

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                tokenProvider.getJwtExpiration(),
                UserResponse.fromEntity(user)
        );
    }

    // ===================== CURRENT USER =====================
    public User getCurrentUser() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", "email", email)
                );
    }
}
