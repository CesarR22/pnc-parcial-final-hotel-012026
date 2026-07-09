package com.uca.pncparcialfinalhotel.controller;

import com.uca.pncparcialfinalhotel.dto.request.RoomAvailabilityRequest;
import com.uca.pncparcialfinalhotel.dto.request.RoomRequest;
import com.uca.pncparcialfinalhotel.dto.response.GeneralResponse;
import com.uca.pncparcialfinalhotel.dto.response.RoomResponse;
import com.uca.pncparcialfinalhotel.service.RoomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public ResponseEntity<GeneralResponse<RoomResponse>> create(@Valid @RequestBody RoomRequest request,
                                                               HttpServletRequest servletRequest) {
        return buildResponse("Room created successfully.", HttpStatus.CREATED, roomService.create(request), servletRequest);
    }

    @GetMapping
    public ResponseEntity<GeneralResponse<List<RoomResponse>>> findAll(@RequestParam(required = false) Long hotelId,
                                                                       HttpServletRequest servletRequest) {
        return buildResponse("Rooms retrieved successfully.", HttpStatus.OK, roomService.findAll(hotelId), servletRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<RoomResponse>> findById(@PathVariable Long id,
                                                                  HttpServletRequest servletRequest) {
        return buildResponse("Room retrieved successfully.", HttpStatus.OK, roomService.findById(id), servletRequest);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public ResponseEntity<GeneralResponse<RoomResponse>> update(@PathVariable Long id,
                                                               @Valid @RequestBody RoomRequest request,
                                                               HttpServletRequest servletRequest) {
        return buildResponse("Room updated successfully.", HttpStatus.OK, roomService.update(id, request), servletRequest);
    }

    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public ResponseEntity<GeneralResponse<RoomResponse>> updateAvailability(@PathVariable Long id,
                                                                           @Valid @RequestBody RoomAvailabilityRequest request,
                                                                           HttpServletRequest servletRequest) {
        return buildResponse("Room availability updated successfully.", HttpStatus.OK, roomService.updateAvailability(id, request), servletRequest);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public ResponseEntity<GeneralResponse<Void>> delete(@PathVariable Long id,
                                                        HttpServletRequest servletRequest) {
        roomService.delete(id);
        return buildResponse("Room deactivated successfully.", HttpStatus.OK, null, servletRequest);
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
