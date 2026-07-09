package com.uca.pncparcialfinalhotel.common.mappers;

import com.uca.pncparcialfinalhotel.dto.request.RoomRequest;
import com.uca.pncparcialfinalhotel.dto.response.RoomResponse;
import com.uca.pncparcialfinalhotel.entities.Hotel;
import com.uca.pncparcialfinalhotel.entities.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public Room toEntity(RoomRequest request, Hotel hotel) {
        return Room.builder()
                .roomNumber(request.getRoomNumber())
                .roomType(request.getRoomType())
                .pricePerNight(request.getPricePerNight())
                .available(request.getAvailable() == null || request.getAvailable())
                .active(true)
                .hotel(hotel)
                .build();
    }

    public void updateEntity(Room room, RoomRequest request, Hotel hotel) {
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(request.getRoomType());
        room.setPricePerNight(request.getPricePerNight());
        room.setAvailable(request.getAvailable() == null || request.getAvailable());
        room.setHotel(hotel);
    }

    public RoomResponse toResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType())
                .pricePerNight(room.getPricePerNight())
                .available(room.getAvailable())
                .active(room.getActive())
                .hotelId(room.getHotel().getId())
                .hotelName(room.getHotel().getName())
                .build();
    }
}
