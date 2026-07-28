package evaanufr.dev.reservationsystem;

import ch.qos.logback.classic.Logger;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.stream.Collectors.toList;

@Service
public class ReservationService {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(ReservationController.class);
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository repo;

    public ReservationService(ReservationRepository repo) {
        this.repo = repo;
    }

    public Reservation getReservationById(Long id) {
        ReservationEntity reservationEntity = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));
        return toDomainReservation(reservationEntity);

    }

    public List<Reservation> findAllReservation() {
        List<ReservationEntity> allReservations = repo.findAll();
        List<Reservation> reservationList = allReservations.stream()
                .map(it -> toDomainReservation(it)
                ).toList();
        return reservationList;
    }

    public Reservation createReservation(Reservation reservationToCreate) {

        if (reservationToCreate.reservationStatus() != null) {
            throw new IllegalArgumentException("Status should be empty");
        }

        if (!reservationToCreate.endDate().isAfter(reservationToCreate.startDate())) {
            throw new IllegalArgumentException("Start date must be one day earlier than end date");
        }

        var entityToSave = new ReservationEntity(null, reservationToCreate.userId(), reservationToCreate.roomId(), reservationToCreate.startDate(), reservationToCreate.endDate(), ReservationStatus.PENDING);
        return toDomainReservation(repo.save(entityToSave));
    }

    public Reservation updateReservation(Long id, Reservation reservationToUpdate) {
        var reservation = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("No such element found with id: " + id));
        if (!reservation.getReservationStatus().equals(ReservationStatus.PENDING)) {
            throw new NoSuchElementException("Cannot modify reservation: status= " + reservation.getReservationStatus());
        }
        if (!reservationToUpdate.endDate().isAfter(reservationToUpdate.startDate())) {
            throw new IllegalArgumentException("Start date must be one day earlier than end date");
        }
        var reservationEntityToUpdate = new ReservationEntity(reservation.getId(), reservationToUpdate.userId(), reservationToUpdate.roomId(), reservationToUpdate.startDate(), reservationToUpdate.endDate(), ReservationStatus.PENDING);
        var updatedReservation = repo.save(reservationEntityToUpdate);
        return toDomainReservation(updatedReservation);

    }

    @Transactional //все модифицирующие запросы
    public void cancelReservation(Long id) {
        var reservation = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found reservation id:" + id));
        if (reservation.getReservationStatus().equals(ReservationStatus.APPROVED)) {
            throw new IllegalStateException("Cannot cancel approved reservation. Contact manager");
        }
        if (reservation.getReservationStatus().equals(ReservationStatus.CANCELLED)) {
            throw new IllegalStateException("Cannot cancel the reservation. Reservation was already cancelled");
        }
        repo.setStatusCanceled(id, ReservationStatus.CANCELLED);
        log.info("Called cancelReservation");
    }

    public Reservation approveReservation(Long id) {
        var reservation = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("No such element found with id: " + id));
        if (!reservation.getReservationStatus().equals(ReservationStatus.PENDING)) {
            throw new NoSuchElementException("Cannot modify reservation: status= " + reservation.getReservationStatus());
        }
        var isConflict = checkIfConflict(reservation);
        if (isConflict) {
            throw new IllegalStateException("Cannot modify reservation: conflicts found");
        }
        reservation.setReservationStatus(ReservationStatus.APPROVED);

        repo.save(reservation);
        return toDomainReservation(reservation);
    }

    public boolean checkIfConflict(ReservationEntity reservation) {
        var allReservations = repo.findAll();

        for (ReservationEntity existingReservation : allReservations) {
            if (reservation.getId() == existingReservation.getId()) {
                continue;
            }
            if (!reservation.getRoomId().equals(existingReservation.getRoomId())) {
                continue;
            }
            if (!existingReservation.getReservationStatus().equals(ReservationStatus.APPROVED)) {
                continue;
            }
            if (reservation.getStartDate().isBefore(existingReservation.getEndDate()) && existingReservation.getStartDate().isBefore(reservation.getEndDate())) {
                return true;
            }

        }
        return false;
    }

    private Reservation toDomainReservation(ReservationEntity reservation) {
        return new Reservation(
                reservation.getId(),
                reservation.getUserId(),
                reservation.getRoomId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getReservationStatus()
        );
    }
}
