package com.uca.pncparcialfinalhotel.repository;

import com.uca.pncparcialfinalhotel.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByHotel_IdAndRoomNumberIgnoreCase(Long hotelId, String roomNumber);
    boolean existsByHotel_IdAndRoomNumberIgnoreCaseAndIdNot(Long hotelId, String roomNumber, Long id);
    List<Room> findByActiveTrue();
    List<Room> findByHotel_IdAndActiveTrue(Long hotelId);
}
