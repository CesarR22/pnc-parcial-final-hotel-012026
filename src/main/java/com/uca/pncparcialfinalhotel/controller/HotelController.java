package com.uca.pncparcialfinalhotel.controller;

import com.uca.pncparcialfinalhotel.dto.request.HotelRequest;
import com.uca.pncparcialfinalhotel.dto.response.GeneralResponse;
import com.uca.pncparcialfinalhotel.dto.response.HotelResponse;
import com.uca.pncparcialfinalhotel.service.HotelService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<HotelResponse>> create(@Valid @RequestBody HotelRequest request,
                                                                 HttpServletRequest servletRequest) {
        HotelResponse data = hotelService.create(request);
        return buildResponse("Hotel created successfully.", HttpStatus.CREATED, data, servletRequest);
    }

    @GetMapping
    public ResponseEntity<GeneralResponse<List<HotelResponse>>> findAll(HttpServletRequest servletRequest) {
        return buildResponse("Hotels retrieved successfully.", HttpStatus.OK, hotelService.findAll(), servletRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<HotelResponse>> findById(@PathVariable Long id,
                                                                   HttpServletRequest servletRequest) {
        return buildResponse("Hotel retrieved successfully.", HttpStatus.OK, hotelService.findById(id), servletRequest);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<HotelResponse>> update(@PathVariable Long id,
                                                                 @Valid @RequestBody HotelRequest request,
                                                                 HttpServletRequest servletRequest) {
        return buildResponse("Hotel updated successfully.", HttpStatus.OK, hotelService.update(id, request), servletRequest);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<Void>> delete(@PathVariable Long id,
                                                        HttpServletRequest servletRequest) {
        hotelService.delete(id);
        return buildResponse("Hotel deactivated successfully.", HttpStatus.OK, null, servletRequest);
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
