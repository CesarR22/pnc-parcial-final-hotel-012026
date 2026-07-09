package com.uca.pncparcialfinalhotel.repository;

import com.uca.pncparcialfinalhotel.entities.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    List<Hotel> findByActiveTrue();
}
