package com.gully.ggos.service;

import com.gully.ggos.api.auth.dto.LoginRequest;
import com.gully.ggos.domain.entity.Org;
import com.gully.ggos.domain.entity.User;
import com.gully.ggos.domain.repository.OrgRepository;
import com.gully.ggos.security.JwtService;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final OrgRepository orgRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
        OrgRepository orgRepository,
        UserService userService,
        PasswordEncoder passwordEncoder,
        JwtService jwtService
    ) {
        this.orgRepository = orgRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String login(LoginRequest request) {
        Org org = orgRepository.findByCode(request.getOrgCode())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Org not found"
            ));
        User user = userService.findByEmailAndOrg(request.getEmail(), org.getId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Invalid credentials"
            ));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Invalid credentials"
            );
        }
        Set<String> roles = user.getRoles().stream()
            .map(role -> role.getCode())
            .collect(Collectors.toSet());
        Set<String> permissions = userService.getPermissions(user);
        return jwtService.generateToken(user.getId(), org.getId(), user.getEmail(), roles, permissions);
    }
}
