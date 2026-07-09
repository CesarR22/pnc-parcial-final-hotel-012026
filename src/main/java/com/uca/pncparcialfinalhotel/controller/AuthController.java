package com.uca.pncparcialfinalhotel.controller;

import com.uca.pncparcialfinalhotel.dto.request.LoginRequest;
import com.uca.pncparcialfinalhotel.dto.request.RefreshTokenRequest;
import com.uca.pncparcialfinalhotel.dto.request.RegisterGuestRequest;
import com.uca.pncparcialfinalhotel.dto.response.AuthResponse;
import com.uca.pncparcialfinalhotel.dto.response.GeneralResponse;
import com.uca.pncparcialfinalhotel.dto.response.UserResponse;
import com.uca.pncparcialfinalhotel.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<GeneralResponse<UserResponse>> registerGuest(@Valid @RequestBody RegisterGuestRequest request,
                                                                       HttpServletRequest servletRequest) {
        UserResponse data = authService.registerGuest(request);
        return buildResponse("Guest registered successfully.", HttpStatus.CREATED, data, servletRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<GeneralResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request,
                                                               HttpServletRequest servletRequest) {
        AuthResponse data = authService.login(request);
        return buildResponse("Login successful.", HttpStatus.OK, data, servletRequest);
    }

    @PostMapping("/refresh")
    public ResponseEntity<GeneralResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request,
                                                                 HttpServletRequest servletRequest) {
        AuthResponse data = authService.refreshAccessToken(request);
        return buildResponse("Access token refreshed successfully.", HttpStatus.OK, data, servletRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<GeneralResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request,
                                                        HttpServletRequest servletRequest) {
        authService.logout(request);
        return buildResponse("Logout successful.", HttpStatus.OK, null, servletRequest);
    }

    private <T> ResponseEntity<GeneralResponse<T>> buildResponse(String message, HttpStatus status, T data, HttpServletRequest request) {
        return ResponseEntity.status(status).body(GeneralResponse.<T>builder()
                .uri(request.getRequestURI())
                .message(message)
                .status(status.value())
                .time(LocalDateTime.now())
                .data(data)
                .build());
    }
}
