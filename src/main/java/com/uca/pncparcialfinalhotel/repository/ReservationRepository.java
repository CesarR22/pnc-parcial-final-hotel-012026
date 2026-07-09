package com.uca.pncparcialfinalhotel.repository;

import com.uca.pncparcialfinalhotel.common.enums.ReservationStatus;
import com.uca.pncparcialfinalhotel.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByGuest_Id(Long guestId);
    List<Reservation> findByRoom_Hotel_Id(Long hotelId);

    @Query("""
        select count(r) > 0
        from Reservation r
        where r.room.id = :roomId
          and r.status in :statuses
          and r.startDate < :endDate
          and r.endDate > :startDate
    """)
    boolean existsOverlappingReservation(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("statuses") Collection<ReservationStatus> statuses
    );

    @Query("""
        select count(r) > 0
        from Reservation r
        where r.room.id = :roomId
          and r.id <> :reservationId
          and r.status in :statuses
          and r.startDate < :endDate
          and r.endDate > :startDate
    """)
    boolean existsOverlappingReservationExcludingCurrent(
            @Param("roomId") Long roomId,
            @Param("reservationId") Long reservationId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("statuses") Collection<ReservationStatus> statuses
    );
}
