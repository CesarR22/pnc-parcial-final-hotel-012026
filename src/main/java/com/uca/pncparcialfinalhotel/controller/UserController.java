package com.uca.pncparcialfinalhotel.controller;

import com.uca.pncparcialfinalhotel.dto.request.UserRequest;
import com.uca.pncparcialfinalhotel.dto.response.GeneralResponse;
import com.uca.pncparcialfinalhotel.dto.response.UserResponse;
import com.uca.pncparcialfinalhotel.service.UserService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<GeneralResponse<UserResponse>> create(@Valid @RequestBody UserRequest request,
                                                                HttpServletRequest servletRequest) {
        return buildResponse("User created successfully.", HttpStatus.CREATED, userService.create(request), servletRequest);
    }

    @GetMapping
    public ResponseEntity<GeneralResponse<List<UserResponse>>> findAll(HttpServletRequest servletRequest) {
        return buildResponse("Users retrieved successfully.", HttpStatus.OK, userService.findAll(), servletRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<UserResponse>> findById(@PathVariable Long id,
                                                                  HttpServletRequest servletRequest) {
        return buildResponse("User retrieved successfully.", HttpStatus.OK, userService.findById(id), servletRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<Void>> deactivate(@PathVariable Long id,
                                                            HttpServletRequest servletRequest) {
        userService.deactivate(id);
        return buildResponse("User deactivated successfully.", HttpStatus.OK, null, servletRequest);
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
