package com.uca.pncparcialfinalhotel.service.impl;

import com.uca.pncparcialfinalhotel.common.enums.UserRole;
import com.uca.pncparcialfinalhotel.common.mappers.RoomMapper;
import com.uca.pncparcialfinalhotel.dto.request.RoomAvailabilityRequest;
import com.uca.pncparcialfinalhotel.dto.request.RoomRequest;
import com.uca.pncparcialfinalhotel.dto.response.RoomResponse;
import com.uca.pncparcialfinalhotel.entities.Hotel;
import com.uca.pncparcialfinalhotel.entities.Room;
import com.uca.pncparcialfinalhotel.entities.User;
import com.uca.pncparcialfinalhotel.exception.BusinessRuleException;
import com.uca.pncparcialfinalhotel.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalhotel.repository.HotelRepository;
import com.uca.pncparcialfinalhotel.repository.RoomRepository;
import com.uca.pncparcialfinalhotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomMapper roomMapper;
    private final SecurityContextService securityContextService;

    @Override
    @Transactional
    public RoomResponse create(RoomRequest request) {
        Hotel hotel = findActiveHotel(request.getHotelId());
        ensureAdminOrReceptionistFromHotel(hotel.getId());

        if (roomRepository.existsByHotel_IdAndRoomNumberIgnoreCase(hotel.getId(), request.getRoomNumber())) {
            throw new BusinessRuleException("Room number already exists in this hotel.");
        }

        Room room = roomMapper.toEntity(request, hotel);
        return roomMapper.toResponse(roomRepository.save(room));
    }

    @Override
    public List<RoomResponse> findAll(Long hotelId) {
        List<Room> rooms = hotelId == null
                ? roomRepository.findByActiveTrue()
                : roomRepository.findByHotel_IdAndActiveTrue(hotelId);

        return rooms.stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    @Override
    public RoomResponse findById(Long id) {
        return roomMapper.toResponse(findActiveRoom(id));
    }

    @Override
    @Transactional
    public RoomResponse update(Long id, RoomRequest request) {
        Room room = findActiveRoom(id);
        Hotel hotel = findActiveHotel(request.getHotelId());
        ensureAdminOrReceptionistFromHotel(room.getHotel().getId());
        ensureAdminOrReceptionistFromHotel(hotel.getId());

        if (roomRepository.existsByHotel_IdAndRoomNumberIgnoreCaseAndIdNot(hotel.getId(), request.getRoomNumber(), id)) {
            throw new BusinessRuleException("Room number already exists in this hotel.");
        }

        roomMapper.updateEntity(room, request, hotel);
        return roomMapper.toResponse(roomRepository.save(room));
    }

    @Override
    @Transactional
    public RoomResponse updateAvailability(Long id, RoomAvailabilityRequest request) {
        Room room = findActiveRoom(id);
        ensureAdminOrReceptionistFromHotel(room.getHotel().getId());
        room.setAvailable(request.getAvailable());
        return roomMapper.toResponse(roomRepository.save(room));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Room room = findActiveRoom(id);
        ensureAdminOrReceptionistFromHotel(room.getHotel().getId());
        room.setActive(false);
        room.setAvailable(false);
        roomRepository.save(room);
    }

    private Hotel findActiveHotel(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));
        if (!Boolean.TRUE.equals(hotel.getActive())) {
            throw new BusinessRuleException("Hotel is inactive.");
        }
        return hotel;
    }

    private Room findActiveRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found."));
        if (!Boolean.TRUE.equals(room.getActive())) {
            throw new ResourceNotFoundException("Room not found.");
        }
        return room;
    }

    private void ensureAdminOrReceptionistFromHotel(Long hotelId) {
        User user = securityContextService.getAuthenticatedUser();
        if (user.getRole() == UserRole.ADMIN) {
            return;
        }
        if (user.getRole() == UserRole.RECEPTIONIST
                && user.getHotel() != null
                && user.getHotel().getId().equals(hotelId)) {
            return;
        }
        throw new AccessDeniedException("User cannot manage resources from another hotel.");
    }
}
