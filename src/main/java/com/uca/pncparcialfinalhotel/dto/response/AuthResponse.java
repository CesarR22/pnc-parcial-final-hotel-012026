package com.uca.pncparcialfinalhotel.dto.response;

import com.uca.pncparcialfinalhotel.common.enums.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponse {
    private Long userId;
    private String fullName;
    private String email;
    private UserRole role;
    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresInMs;
}
