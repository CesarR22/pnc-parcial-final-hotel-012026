package com.uca.pncparcialfinalhotel.common.mappers;

import com.uca.pncparcialfinalhotel.dto.response.ReservationResponse;
import com.uca.pncparcialfinalhotel.entities.Reservation;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public ReservationResponse toResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .guestId(reservation.getGuest().getId())
                .guestName(reservation.getGuest().getFullName())
                .roomId(reservation.getRoom().getId())
                .roomNumber(reservation.getRoom().getRoomNumber())
                .hotelId(reservation.getRoom().getHotel().getId())
                .hotelName(reservation.getRoom().getHotel().getName())
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .status(reservation.getStatus())
                .totalPrice(reservation.getTotalPrice())
                .createdAt(reservation.getCreatedAt())
                .build();
    }
}
