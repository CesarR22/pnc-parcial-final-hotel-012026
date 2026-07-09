package com.uca.pncparcialfinalhotel.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomAvailabilityRequest {
    @NotNull(message = "Available value is required.")
    private Boolean available;
}
