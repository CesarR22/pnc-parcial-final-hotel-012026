package com.uca.pncparcialfinalhotel.service.impl;

import com.uca.pncparcialfinalhotel.common.enums.ReservationStatus;
import com.uca.pncparcialfinalhotel.common.enums.UserRole;
import com.uca.pncparcialfinalhotel.common.mappers.ReservationMapper;
import com.uca.pncparcialfinalhotel.dto.request.ReservationRequest;
import com.uca.pncparcialfinalhotel.dto.response.ReservationResponse;
import com.uca.pncparcialfinalhotel.entities.Reservation;
import com.uca.pncparcialfinalhotel.entities.Room;
import com.uca.pncparcialfinalhotel.entities.User;
import com.uca.pncparcialfinalhotel.exception.BusinessRuleException;
import com.uca.pncparcialfinalhotel.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalhotel.repository.ReservationRepository;
import com.uca.pncparcialfinalhotel.repository.RoomRepository;
import com.uca.pncparcialfinalhotel.repository.UserRepository;
import com.uca.pncparcialfinalhotel.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ReservationMapper reservationMapper;
    private final SecurityContextService securityContextService;

    @Override
    @Transactional
    public ReservationResponse create(ReservationRequest request) {
        User currentUser = securityContextService.getAuthenticatedUser();
        Room room = findActiveRoom(request.getRoomId());
        validateDates(request.getStartDate(), request.getEndDate());

        User guest = resolveGuestForCreate(currentUser, request.getGuestId());
        ensureCanCreateReservationForRoom(currentUser, room);
        validateRoomCanBeReserved(room, request.getStartDate(), request.getEndDate());

        Reservation reservation = Reservation.builder()
                .guest(guest)
                .room(room)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(ReservationStatus.PENDING)
                .totalPrice(calculateTotalPrice(room, request.getStartDate(), request.getEndDate()))
                .createdAt(LocalDateTime.now())
                .build();

        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    public List<ReservationResponse> findVisibleReservations() {
        User currentUser = securityContextService.getAuthenticatedUser();

        if (currentUser.getRole() == UserRole.ADMIN) {
            return reservationRepository.findAll().stream()
                    .map(reservationMapper::toResponse)
                    .toList();
        }

        if (currentUser.getRole() == UserRole.RECEPTIONIST) {
            if (currentUser.getHotel() == null) {
                throw new AccessDeniedException("Receptionist has no assigned hotel.");
            }
            return reservationRepository.findByRoom_Hotel_Id(currentUser.getHotel().getId()).stream()
                    .map(reservationMapper::toResponse)
                    .toList();
        }

        return reservationRepository.findByGuest_Id(currentUser.getId()).stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @Override
    public List<ReservationResponse> findMyReservations() {
        User currentUser = securityContextService.getAuthenticatedUser();
        return reservationRepository.findByGuest_Id(currentUser.getId()).stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    @Override
    public ReservationResponse findById(Long id) {
        Reservation reservation = findReservation(id);
        ensureCanViewReservation(reservation);
        return reservationMapper.toResponse(reservation);
    }

    @Override
    @Transactional
    public ReservationResponse update(Long id, ReservationRequest request) {
        Reservation reservation = findReservation(id);
        ensureReceptionistOrAdminForReservationHotel(reservation);

        Room newRoom = findActiveRoom(request.getRoomId());
        ensureReceptionistOrAdminForHotel(newRoom.getHotel().getId());
        validateDates(request.getStartDate(), request.getEndDate());

        if (reservationRepository.existsOverlappingReservationExcludingCurrent(
                newRoom.getId(),
                reservation.getId(),
                request.getStartDate(),
                request.getEndDate(),
                List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED))) {
            throw new BusinessRuleException("Room already has a reservation in the selected date range.");
        }

        reservation.setRoom(newRoom);
        reservation.setStartDate(request.getStartDate());
        reservation.setEndDate(request.getEndDate());
        reservation.setTotalPrice(calculateTotalPrice(newRoom, request.getStartDate(), request.getEndDate()));

        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    @Transactional
    public ReservationResponse confirm(Long id) {
        Reservation reservation = findReservation(id);
        ensureReceptionistOrAdminForReservationHotel(reservation);

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessRuleException("Cancelled reservations cannot be confirmed.");
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    @Transactional
    public ReservationResponse cancel(Long id) {
        Reservation reservation = findReservation(id);
        ensureCanCancelReservation(reservation);

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessRuleException("Reservation is already cancelled.");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    private User resolveGuestForCreate(User currentUser, Long guestId) {
        if (currentUser.getRole() == UserRole.GUEST) {
            return currentUser;
        }

        if (guestId == null) {
            throw new BusinessRuleException("Guest id is required when ADMIN or RECEPTIONIST creates a reservation.");
        }

        User guest = userRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFoundException("Guest user not found."));

        if (guest.getRole() != UserRole.GUEST) {
            throw new BusinessRuleException("Reservation guest must have GUEST role.");
        }

        return guest;
    }

    private void ensureCanCreateReservationForRoom(User user, Room room) {
        if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.GUEST) {
            return;
        }

        if (user.getRole() == UserRole.RECEPTIONIST
                && user.getHotel() != null
                && user.getHotel().getId().equals(room.getHotel().getId())) {
            return;
        }

        throw new AccessDeniedException("Receptionist can only create reservations for their own hotel.");
    }

    private void ensureCanViewReservation(Reservation reservation) {
        User user = securityContextService.getAuthenticatedUser();
        if (user.getRole() == UserRole.ADMIN) {
            return;
        }
        if (user.getRole() == UserRole.GUEST && reservation.getGuest().getId().equals(user.getId())) {
            return;
        }
        if (user.getRole() == UserRole.RECEPTIONIST
                && user.getHotel() != null
                && reservation.getRoom().getHotel().getId().equals(user.getHotel().getId())) {
            return;
        }
        throw new AccessDeniedException("User cannot view this reservation.");
    }

    private void ensureCanCancelReservation(Reservation reservation) {
        User user = securityContextService.getAuthenticatedUser();
        if (user.getRole() == UserRole.GUEST && reservation.getGuest().getId().equals(user.getId())) {
            return;
        }
        ensureReceptionistOrAdminForReservationHotel(reservation);
    }

    private void ensureReceptionistOrAdminForReservationHotel(Reservation reservation) {
        ensureReceptionistOrAdminForHotel(reservation.getRoom().getHotel().getId());
    }

    private void ensureReceptionistOrAdminForHotel(Long hotelId) {
        User user = securityContextService.getAuthenticatedUser();
        if (user.getRole() == UserRole.ADMIN) {
            return;
        }
        if (user.getRole() == UserRole.RECEPTIONIST
                && user.getHotel() != null
                && user.getHotel().getId().equals(hotelId)) {
            return;
        }
        throw new AccessDeniedException("Receptionist can only manage reservations from their own hotel.");
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (endDate == null || startDate == null) {
            throw new BusinessRuleException("Reservation dates are required.");
        }
        if (!endDate.isAfter(startDate)) {
            throw new BusinessRuleException("End date must be after start date.");
        }
    }

    private void validateRoomCanBeReserved(Room room, LocalDate startDate, LocalDate endDate) {
        if (!Boolean.TRUE.equals(room.getAvailable())) {
            throw new BusinessRuleException("Room is not available.");
        }

        boolean existsOverlap = reservationRepository.existsOverlappingReservation(
                room.getId(),
                startDate,
                endDate,
                List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)
        );

        if (existsOverlap) {
            throw new BusinessRuleException("Room already has a reservation in the selected date range.");
        }
    }

    private BigDecimal calculateTotalPrice(Room room, LocalDate startDate, LocalDate endDate) {
        long nights = ChronoUnit.DAYS.between(startDate, endDate);
        return room.getPricePerNight().multiply(BigDecimal.valueOf(nights));
    }

    private Room findActiveRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found."));
        if (!Boolean.TRUE.equals(room.getActive())) {
            throw new ResourceNotFoundException("Room not found.");
        }
        return room;
    }

    private Reservation findReservation(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found."));
    }
}
