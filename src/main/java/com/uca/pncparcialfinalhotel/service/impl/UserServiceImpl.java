package com.uca.pncparcialfinalhotel.service.impl;

import com.uca.pncparcialfinalhotel.common.enums.UserRole;
import com.uca.pncparcialfinalhotel.common.mappers.UserMapper;
import com.uca.pncparcialfinalhotel.dto.request.UserRequest;
import com.uca.pncparcialfinalhotel.dto.response.UserResponse;
import com.uca.pncparcialfinalhotel.entities.Hotel;
import com.uca.pncparcialfinalhotel.entities.User;
import com.uca.pncparcialfinalhotel.exception.BusinessRuleException;
import com.uca.pncparcialfinalhotel.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalhotel.repository.HotelRepository;
import com.uca.pncparcialfinalhotel.repository.UserRepository;
import com.uca.pncparcialfinalhotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new BusinessRuleException("Email is already registered.");
        }

        Hotel hotel = null;
        if (request.getRole() == UserRole.RECEPTIONIST) {
            if (request.getHotelId() == null) {
                throw new BusinessRuleException("Receptionist user must belong to a hotel.");
            }
            hotel = hotelRepository.findById(request.getHotelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hotel not found."));
            if (!Boolean.TRUE.equals(hotel.getActive())) {
                throw new BusinessRuleException("Hotel is inactive.");
            }
        }

        if (request.getRole() != UserRole.RECEPTIONIST && request.getHotelId() != null) {
            throw new BusinessRuleException("Only receptionist users can be assigned to a hotel.");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .hotel(hotel)
                .active(true)
                .build();

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse findById(Long id) {
        return userMapper.toResponse(findUserEntity(id));
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        User user = findUserEntity(id);
        user.setActive(false);
        userRepository.save(user);
    }

    private User findUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }
}
