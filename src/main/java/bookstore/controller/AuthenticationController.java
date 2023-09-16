package bookstore.controller;

import bookstore.dto.user.UserLoginRequestDto;
import bookstore.dto.user.UserLoginResponseDto;
import bookstore.dto.user.UserRegistrationRequestDto;
import bookstore.dto.user.UserResponseDto;
import bookstore.exception.RegistrationException;
import bookstore.security.AuthenticationService;
import bookstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication management", description = "Endpoints for login and register new user")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Login user")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Register new user")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto request)
            throws RegistrationException {
        return userService.register(request);
    }
}
