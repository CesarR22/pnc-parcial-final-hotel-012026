package com.uca.pncparcialfinalhotel.dto.request;

import com.uca.pncparcialfinalhotel.common.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    @NotBlank(message = "Full name is required.")
    private String fullName;

    @Email(message = "Email must have a valid format.")
    @NotBlank(message = "Email is required.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, message = "Password must have at least 6 characters.")
    private String password;

    @NotNull(message = "Role is required.")
    private UserRole role;

    private Long hotelId;
}
