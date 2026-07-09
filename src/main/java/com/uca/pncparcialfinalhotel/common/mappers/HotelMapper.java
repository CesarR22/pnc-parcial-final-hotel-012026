package com.uca.pncparcialfinalhotel.common.mappers;

import com.uca.pncparcialfinalhotel.dto.request.HotelRequest;
import com.uca.pncparcialfinalhotel.dto.response.HotelResponse;
import com.uca.pncparcialfinalhotel.entities.Hotel;
import org.springframework.stereotype.Component;

@Component
public class HotelMapper {

    public Hotel toEntity(HotelRequest request) {
        return Hotel.builder()
                .name(request.getName())
                .address(request.getAddress())
                .active(true)
                .build();
    }

    public void updateEntity(Hotel hotel, HotelRequest request) {
        hotel.setName(request.getName());
        hotel.setAddress(request.getAddress());
    }

    public HotelResponse toResponse(Hotel hotel) {
        return HotelResponse.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .address(hotel.getAddress())
                .active(hotel.getActive())
                .build();
    }
}
