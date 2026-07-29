package evaanufr.dev.reservationsystem.reservations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
//    @Query(value = "select * from ReservationEntity r where r.reservationStatus =:status", nativeQuery = true)
//    List<ReservationEntity> findAllByStatusIs(ReservationStatus status);
//
//    @Query("select r from ReservationEntity r where r.roomId=:roomId")
//    List<ReservationEntity> findAllByRoomId(@Param("roomId") Long roomId);

    @Modifying
    @Query("update ReservationEntity r set r.reservationStatus=:status where r.id=:id")
    void setStatusCanceled(@Param("id") Long id, @Param("status") ReservationStatus status);

    @Query("select r.id from ReservationEntity r where r.roomId =:roomId and :startDate<r.endDate and r.startDate<:endDate and r.reservationStatus=:status")
    List<Long> findConflictReservationIds(@Param("id") Long roomId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("status") ReservationStatus status);
}
