package com.uca.pncparcialfinalhotel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelRequest {
    @NotBlank(message = "Hotel name is required.")
    @Size(max = 120, message = "Hotel name must be at most 120 characters.")
    private String name;

    @NotBlank(message = "Hotel address is required.")
    @Size(max = 180, message = "Hotel address must be at most 180 characters.")
    private String address;
}
