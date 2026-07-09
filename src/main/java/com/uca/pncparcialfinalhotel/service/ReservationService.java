package com.uca.pncparcialfinalhotel.service;

import com.uca.pncparcialfinalhotel.dto.request.ReservationRequest;
import com.uca.pncparcialfinalhotel.dto.response.ReservationResponse;

import java.util.List;

public interface ReservationService {
    ReservationResponse create(ReservationRequest request);
    List<ReservationResponse> findVisibleReservations();
    List<ReservationResponse> findMyReservations();
    ReservationResponse findById(Long id);
    ReservationResponse update(Long id, ReservationRequest request);
    ReservationResponse confirm(Long id);
    ReservationResponse cancel(Long id);
}
