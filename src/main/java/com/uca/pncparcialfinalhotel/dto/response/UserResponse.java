package com.uca.pncparcialfinalhotel.dto.response;

import com.uca.pncparcialfinalhotel.common.enums.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private UserRole role;
    private Boolean active;
    private Long hotelId;
    private String hotelName;
}
