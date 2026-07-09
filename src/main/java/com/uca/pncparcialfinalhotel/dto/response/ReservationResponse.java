package com.uca.pncparcialfinalhotel.dto.response;

import com.uca.pncparcialfinalhotel.common.enums.ReservationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ReservationResponse {
    private Long id;
    private Long guestId;
    private String guestName;
    private Long roomId;
    private String roomNumber;
    private Long hotelId;
    private String hotelName;
    private LocalDate startDate;
    private LocalDate endDate;
    private ReservationStatus status;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
}
