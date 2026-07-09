package com.uca.pncparcialfinalhotel.service;

import com.uca.pncparcialfinalhotel.dto.request.LoginRequest;
import com.uca.pncparcialfinalhotel.dto.request.RefreshTokenRequest;
import com.uca.pncparcialfinalhotel.dto.request.RegisterGuestRequest;
import com.uca.pncparcialfinalhotel.dto.response.AuthResponse;
import com.uca.pncparcialfinalhotel.dto.response.UserResponse;

public interface AuthService {
    UserResponse registerGuest(RegisterGuestRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshAccessToken(RefreshTokenRequest request);
    void logout(RefreshTokenRequest request);
}
