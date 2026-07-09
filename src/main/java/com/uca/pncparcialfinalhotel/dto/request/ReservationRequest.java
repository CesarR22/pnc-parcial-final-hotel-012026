package com.uca.pncparcialfinalhotel.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReservationRequest {
    @NotNull(message = "Room id is required.")
    private Long roomId;

    private Long guestId;

    @NotNull(message = "Start date is required.")
    @FutureOrPresent(message = "Start date must be today or future.")
    private LocalDate startDate;

    @NotNull(message = "End date is required.")
    private LocalDate endDate;
}
