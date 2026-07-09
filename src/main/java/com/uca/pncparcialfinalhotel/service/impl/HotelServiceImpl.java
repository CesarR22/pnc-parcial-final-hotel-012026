package com.uca.pncparcialfinalhotel.service.impl;

import com.uca.pncparcialfinalhotel.common.mappers.HotelMapper;
import com.uca.pncparcialfinalhotel.dto.request.HotelRequest;
import com.uca.pncparcialfinalhotel.dto.response.HotelResponse;
import com.uca.pncparcialfinalhotel.entities.Hotel;
import com.uca.pncparcialfinalhotel.exception.BusinessRuleException;
import com.uca.pncparcialfinalhotel.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalhotel.repository.HotelRepository;
import com.uca.pncparcialfinalhotel.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;

    @Override
    @Transactional
    public HotelResponse create(HotelRequest request) {
        if (hotelRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BusinessRuleException("Hotel name is already registered.");
        }
        Hotel hotel = hotelMapper.toEntity(request);
        return hotelMapper.toResponse(hotelRepository.save(hotel));
    }

    @Override
    public List<HotelResponse> findAll() {
        return hotelRepository.findByActiveTrue()
                .stream()
                .map(hotelMapper::toResponse)
                .toList();
    }

    @Override
    public HotelResponse findById(Long id) {
        return hotelMapper.toResponse(findHotelEntity(id));
    }

    @Override
    @Transactional
    public HotelResponse update(Long id, HotelRequest request) {
        Hotel hotel = findHotelEntity(id);
        if (hotelRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new BusinessRuleException("Hotel name is already registered.");
        }
        hotelMapper.updateEntity(hotel, request);
        return hotelMapper.toResponse(hotelRepository.save(hotel));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Hotel hotel = findHotelEntity(id);
        hotel.setActive(false);
        hotelRepository.save(hotel);
    }

    private Hotel findHotelEntity(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));
        if (!Boolean.TRUE.equals(hotel.getActive())) {
            throw new ResourceNotFoundException("Hotel not found.");
        }
        return hotel;
    }
}
