package com.uca.pncparcialfinalhotel.dto.response;

import com.uca.pncparcialfinalhotel.common.enums.RoomType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class RoomResponse {
    private Long id;
    private String roomNumber;
    private RoomType roomType;
    private BigDecimal pricePerNight;
    private Boolean available;
    private Boolean active;
    private Long hotelId;
    private String hotelName;
}
