package evaanufr.dev.reservationsystem.reservations;

import ch.qos.logback.classic.Logger;
import evaanufr.dev.reservationsystem.reservations.availability.ReservationAvailabilityService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReservationService {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(ReservationController.class);
    private final ReservationMapper reservationMapper;
    private final ReservationAvailabilityService reservationAvailabilityService;
    private final ReservationRepository repo;

    public ReservationService(ReservationMapper reservationMapper, ReservationAvailabilityService reservationAvailabilityService, ReservationRepository repo) {
        this.reservationMapper = reservationMapper;
        this.reservationAvailabilityService = reservationAvailabilityService;
        this.repo = repo;
    }

    public Reservation getReservationById(Long id) {
        ReservationEntity reservationEntity = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));
        return reservationMapper.toDomain(reservationEntity);

    }

    public List<Reservation> searchAllByFilter(ReservationFilter filter) {
        int pageSize = filter.pageSize() != null ? filter.pageSize() : 10;
        int pageNumber = filter.pageNumber() != null ? filter.pageNumber() : 0;
        var pageable = Pageable.ofSize(pageSize).withPage(pageNumber);

        List<ReservationEntity> allReservations = repo.searchAllByFilter(filter.roomId(), filter.userId(), pageable);
        List<Reservation> reservationList = allReservations.stream()
                .map(reservationMapper::toDomain).toList();
        return reservationList;
    }

    public Reservation createReservation(Reservation reservationToCreate) {

        if (reservationToCreate.reservationStatus() != null) {
            throw new IllegalArgumentException("Status should be empty");
        }

        if (!reservationToCreate.endDate().isAfter(reservationToCreate.startDate())) {
            throw new IllegalArgumentException("Start date must be one day earlier than end date");
        }
        var resToCreate = reservationMapper.toEntity(reservationToCreate);
        resToCreate.setId(null);
        resToCreate.setReservationStatus(ReservationStatus.PENDING);
        return reservationMapper.toDomain(repo.save(resToCreate));
    }

    public Reservation updateReservation(Long id, Reservation reservationToUpdate) {
        var reservation = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("No such element found with id: " + id));
        if (!reservation.getReservationStatus().equals(ReservationStatus.PENDING)) {
            throw new NoSuchElementException("Cannot modify reservation: status= " + reservation.getReservationStatus());
        }
        if (!reservationToUpdate.endDate().isAfter(reservationToUpdate.startDate())) {
            throw new IllegalArgumentException("Start date must be one day earlier than end date");
        }
        var resToUpdate = reservationMapper.toEntity(reservationToUpdate);
        resToUpdate.setId(reservation.getId());
        resToUpdate.setReservationStatus(ReservationStatus.PENDING);
        var updatedReservation = repo.save(resToUpdate);
        return reservationMapper.toDomain(updatedReservation);

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
        logger.info("Called cancelReservation");
    }

    public Reservation approveReservation(Long id) {
        var reservation = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("No such element found with id: " + id));
        if (!reservation.getReservationStatus().equals(ReservationStatus.PENDING)) {
            throw new NoSuchElementException("Cannot modify reservation: status= " + reservation.getReservationStatus());
        }
        var isAvailable = reservationAvailabilityService.checkIfAvailable(reservation.getRoomId(), reservation.getStartDate(), reservation.getEndDate());
        if (!isAvailable) {
            throw new IllegalStateException("Cannot modify reservation: conflicts found");
        }
        reservation.setReservationStatus(ReservationStatus.APPROVED);

        repo.save(reservation);
        return reservationMapper.toDomain(reservation);
    }


}
