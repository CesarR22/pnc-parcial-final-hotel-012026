package com.uca.pncparcialfinalhotel.service;

import com.uca.pncparcialfinalhotel.dto.request.UserRequest;
import com.uca.pncparcialfinalhotel.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse create(UserRequest request);
    List<UserResponse> findAll();
    UserResponse findById(Long id);
    void deactivate(Long id);
}
