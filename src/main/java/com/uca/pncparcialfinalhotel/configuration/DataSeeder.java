package com.uca.pncparcialfinalhotel.configuration;

import com.uca.pncparcialfinalhotel.common.enums.RoomType;
import com.uca.pncparcialfinalhotel.common.enums.UserRole;
import com.uca.pncparcialfinalhotel.entities.Hotel;
import com.uca.pncparcialfinalhotel.entities.Room;
import com.uca.pncparcialfinalhotel.entities.User;
import com.uca.pncparcialfinalhotel.repository.HotelRepository;
import com.uca.pncparcialfinalhotel.repository.RoomRepository;
import com.uca.pncparcialfinalhotel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedData(HotelRepository hotelRepository,
                               RoomRepository roomRepository,
                               UserRepository userRepository) {
        return args -> {
            Hotel centralHotel = hotelRepository.findByActiveTrue()
                    .stream()
                    .filter(hotel -> hotel.getName().equalsIgnoreCase("Hotel Central"))
                    .findFirst()
                    .orElseGet(() -> hotelRepository.save(Hotel.builder()
                            .name("Hotel Central")
                            .address("San Salvador")
                            .active(true)
                            .build()));

            Hotel beachHotel = hotelRepository.findByActiveTrue()
                    .stream()
                    .filter(hotel -> hotel.getName().equalsIgnoreCase("Hotel Playa"))
                    .findFirst()
                    .orElseGet(() -> hotelRepository.save(Hotel.builder()
                            .name("Hotel Playa")
                            .address("La Libertad")
                            .active(true)
                            .build()));

            if (!roomRepository.existsByHotel_IdAndRoomNumberIgnoreCase(centralHotel.getId(), "101")) {
                roomRepository.save(Room.builder()
                        .roomNumber("101")
                        .roomType(RoomType.SINGLE)
                        .pricePerNight(new BigDecimal("55.00"))
                        .available(true)
                        .active(true)
                        .hotel(centralHotel)
                        .build());
            }

            if (!roomRepository.existsByHotel_IdAndRoomNumberIgnoreCase(centralHotel.getId(), "201")) {
                roomRepository.save(Room.builder()
                        .roomNumber("201")
                        .roomType(RoomType.DOUBLE)
                        .pricePerNight(new BigDecimal("85.00"))
                        .available(true)
                        .active(true)
                        .hotel(centralHotel)
                        .build());
            }

            if (!roomRepository.existsByHotel_IdAndRoomNumberIgnoreCase(beachHotel.getId(), "301")) {
                roomRepository.save(Room.builder()
                        .roomNumber("301")
                        .roomType(RoomType.SUITE)
                        .pricePerNight(new BigDecimal("150.00"))
                        .available(true)
                        .active(true)
                        .hotel(beachHotel)
                        .build());
            }

            createUserIfNotExists(userRepository, "Admin Hotel", "admin@hotel.com", UserRole.ADMIN, null);
            createUserIfNotExists(userRepository, "Recepcionista Central", "reception@hotel.com", UserRole.RECEPTIONIST, centralHotel);
            createUserIfNotExists(userRepository, "Huesped Demo", "guest@hotel.com", UserRole.GUEST, null);
        };
    }

    private void createUserIfNotExists(UserRepository userRepository,
                                       String fullName,
                                       String email,
                                       UserRole role,
                                       Hotel hotel) {
        if (!userRepository.existsByEmailIgnoreCase(email)) {
            userRepository.save(User.builder()
                    .fullName(fullName)
                    .email(email)
                    .password(passwordEncoder.encode("123456"))
                    .role(role)
                    .hotel(hotel)
                    .active(true)
                    .build());
        }
    }
}
