package com.uca.pncparcialfinalhotel.controller;

import com.uca.pncparcialfinalhotel.dto.request.ReservationRequest;
import com.uca.pncparcialfinalhotel.dto.response.GeneralResponse;
import com.uca.pncparcialfinalhotel.dto.response.ReservationResponse;
import com.uca.pncparcialfinalhotel.service.ReservationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','GUEST')")
    public ResponseEntity<GeneralResponse<ReservationResponse>> create(@Valid @RequestBody ReservationRequest request,
                                                                       HttpServletRequest servletRequest) {
        return buildResponse("Reservation created successfully.", HttpStatus.CREATED, reservationService.create(request), servletRequest);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','GUEST')")
    public ResponseEntity<GeneralResponse<List<ReservationResponse>>> findVisibleReservations(HttpServletRequest servletRequest) {
        return buildResponse("Reservations retrieved successfully.", HttpStatus.OK, reservationService.findVisibleReservations(), servletRequest);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('GUEST')")
    public ResponseEntity<GeneralResponse<List<ReservationResponse>>> findMyReservations(HttpServletRequest servletRequest) {
        return buildResponse("Guest reservations retrieved successfully.", HttpStatus.OK, reservationService.findMyReservations(), servletRequest);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','GUEST')")
    public ResponseEntity<GeneralResponse<ReservationResponse>> findById(@PathVariable Long id,
                                                                         HttpServletRequest servletRequest) {
        return buildResponse("Reservation retrieved successfully.", HttpStatus.OK, reservationService.findById(id), servletRequest);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public ResponseEntity<GeneralResponse<ReservationResponse>> update(@PathVariable Long id,
                                                                       @Valid @RequestBody ReservationRequest request,
                                                                       HttpServletRequest servletRequest) {
        return buildResponse("Reservation updated successfully.", HttpStatus.OK, reservationService.update(id, request), servletRequest);
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public ResponseEntity<GeneralResponse<ReservationResponse>> confirm(@PathVariable Long id,
                                                                        HttpServletRequest servletRequest) {
        return buildResponse("Reservation confirmed successfully.", HttpStatus.OK, reservationService.confirm(id), servletRequest);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','GUEST')")
    public ResponseEntity<GeneralResponse<ReservationResponse>> cancel(@PathVariable Long id,
                                                                       HttpServletRequest servletRequest) {
        return buildResponse("Reservation cancelled successfully.", HttpStatus.OK, reservationService.cancel(id), servletRequest);
    }

    private <T> ResponseEntity<GeneralResponse<T>> buildResponse(String message, HttpStatus status, T data, HttpServletRequest request) {
        return ResponseEntity.status(status).body(GeneralResponse.<T>builder()
                .uri(request.getRequestURI())
                .message(message)
                .status(status.value())
                .time(LocalDateTime.now())
                .data(data)
                .build());
    }
}
