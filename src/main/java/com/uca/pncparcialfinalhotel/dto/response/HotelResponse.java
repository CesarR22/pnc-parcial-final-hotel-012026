package com.uca.pncparcialfinalhotel.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class HotelResponse {
    private Long id;
    private String name;
    private String address;
    private Boolean active;
}
