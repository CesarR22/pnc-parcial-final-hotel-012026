package com.uca.pncparcialfinalhotel.service.impl;

import com.uca.pncparcialfinalhotel.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityContextService {

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new IllegalStateException("Authenticated user not found.");
        }
        return user;
    }
}
