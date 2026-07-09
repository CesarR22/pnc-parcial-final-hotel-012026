package com.uca.pncparcialfinalhotel.common.mappers;

import com.uca.pncparcialfinalhotel.dto.response.UserResponse;
import com.uca.pncparcialfinalhotel.entities.Hotel;
import com.uca.pncparcialfinalhotel.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        Hotel hotel = user.getHotel();
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.getActive())
                .hotelId(hotel != null ? hotel.getId() : null)
                .hotelName(hotel != null ? hotel.getName() : null)
                .build();
    }
}
