package com.uca.pncparcialfinalhotel.service;

import com.uca.pncparcialfinalhotel.dto.request.RoomAvailabilityRequest;
import com.uca.pncparcialfinalhotel.dto.request.RoomRequest;
import com.uca.pncparcialfinalhotel.dto.response.RoomResponse;

import java.util.List;

public interface RoomService {
    RoomResponse create(RoomRequest request);
    List<RoomResponse> findAll(Long hotelId);
    RoomResponse findById(Long id);
    RoomResponse update(Long id, RoomRequest request);
    RoomResponse updateAvailability(Long id, RoomAvailabilityRequest request);
    void delete(Long id);
}
