package com.gully.ggos.api.auth;

import com.gully.ggos.api.auth.dto.LoginRequest;
import com.gully.ggos.api.auth.dto.LoginResponse;
import com.gully.ggos.api.auth.dto.MeResponse;
import com.gully.ggos.domain.entity.User;
import com.gully.ggos.service.AuthService;
import com.gully.ggos.service.CurrentUserService;
import com.gully.ggos.service.LoginRateLimiter;
import com.gully.ggos.service.UserService;
import jakarta.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final CurrentUserService currentUserService;
    private final UserService userService;
    private final LoginRateLimiter loginRateLimiter;
    private final HttpServletRequest request;

    public AuthController(
        AuthService authService,
        CurrentUserService currentUserService,
        UserService userService,
        LoginRateLimiter loginRateLimiter,
        HttpServletRequest request
    ) {
        this.authService = authService;
        this.currentUserService = currentUserService;
        this.userService = userService;
        this.loginRateLimiter = loginRateLimiter;
        this.request = request;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String key = request.getEmail() + ":" + this.request.getRemoteAddr();
        loginRateLimiter.checkRateLimit(key);
        String token = authService.login(request);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @GetMapping("/me")
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    public ResponseEntity<MeResponse> me() {
        User user = userService.getUserOrThrow(currentUserService.getUserId());
        Set<String> roles = user.getRoles().stream()
            .map(role -> role.getCode())
            .collect(Collectors.toSet());
        Set<String> permissions = userService.getPermissions(user);
        return ResponseEntity.ok(new MeResponse(
            user.getId(),
            user.getOrg().getId(),
            user.getEmail(),
            user.getFullName(),
            roles,
            permissions
        ));
    }
}
