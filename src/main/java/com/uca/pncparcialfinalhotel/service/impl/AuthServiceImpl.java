package com.uca.pncparcialfinalhotel.service.impl;

import com.uca.pncparcialfinalhotel.common.enums.UserRole;
import com.uca.pncparcialfinalhotel.common.mappers.UserMapper;
import com.uca.pncparcialfinalhotel.dto.request.LoginRequest;
import com.uca.pncparcialfinalhotel.dto.request.RefreshTokenRequest;
import com.uca.pncparcialfinalhotel.dto.request.RegisterGuestRequest;
import com.uca.pncparcialfinalhotel.dto.response.AuthResponse;
import com.uca.pncparcialfinalhotel.dto.response.UserResponse;
import com.uca.pncparcialfinalhotel.entities.RefreshToken;
import com.uca.pncparcialfinalhotel.entities.User;
import com.uca.pncparcialfinalhotel.exception.BusinessRuleException;
import com.uca.pncparcialfinalhotel.repository.RefreshTokenRepository;
import com.uca.pncparcialfinalhotel.repository.UserRepository;
import com.uca.pncparcialfinalhotel.security.JwtTokenProvider;
import com.uca.pncparcialfinalhotel.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Value("${app.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Override
    @Transactional
    public UserResponse registerGuest(RegisterGuestRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new BusinessRuleException("Email is already registered.");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.GUEST)
                .active(true)
                .hotel(null)
                .build();

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = (User) authentication.getPrincipal();
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        RefreshToken refreshToken = createRefreshToken(user);

        return buildAuthResponse(user, accessToken, refreshToken.getToken());
    }

    @Override
    @Transactional
    public AuthResponse refreshAccessToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BusinessRuleException("Invalid refresh token."));

        if (Boolean.TRUE.equals(refreshToken.getRevoked())) {
            throw new BusinessRuleException("Refresh token has been revoked.");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new BusinessRuleException("Refresh token has expired.");
        }

        User user = refreshToken.getUser();
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new BusinessRuleException("User is inactive.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        return buildAuthResponse(user, accessToken, refreshToken.getToken());
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokenRepository.findByToken(request.getRefreshToken()).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID() + "." + UUID.randomUUID())
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plus(Duration.ofMillis(refreshTokenExpirationMs)))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresInMs(jwtTokenProvider.getAccessExpirationMs())
                .build();
    }
}
