package com.uca.pncparcialfinalhotel.dto.request;

import com.uca.pncparcialfinalhotel.common.enums.RoomType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RoomRequest {
    @NotBlank(message = "Room number is required.")
    private String roomNumber;

    @NotNull(message = "Room type is required.")
    private RoomType roomType;

    @NotNull(message = "Price per night is required.")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0.")
    private BigDecimal pricePerNight;

    @NotNull(message = "Hotel id is required.")
    private Long hotelId;

    private Boolean available;
}
