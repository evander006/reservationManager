package evaanufr.dev.reservationsystem.reservations.availability;

import ch.qos.logback.classic.Logger;
import evaanufr.dev.reservationsystem.reservations.ReservationController;
import evaanufr.dev.reservationsystem.reservations.ReservationRepository;
import evaanufr.dev.reservationsystem.reservations.ReservationStatus;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationAvailabilityService {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(ReservationAvailabilityService.class);
    private final ReservationRepository repo;

    public ReservationAvailabilityService(ReservationRepository repo) {
        this.repo = repo;
    }

    public boolean checkIfAvailable(Long roomId, LocalDate startDate, LocalDate endDate) {
        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("Start date must be one day earlier than end date");
        }
        List<Long> conflictIds = repo.findConflictReservationIds(roomId, startDate, endDate, ReservationStatus.APPROVED);
        if (conflictIds.isEmpty()) {
            return true;
        } else {
            logger.info("Conflict with ids ={}", conflictIds);
            return false;
        }
    }
}
