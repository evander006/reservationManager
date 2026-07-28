package evaanufr.dev.reservationsystem;


import ch.qos.logback.classic.Logger;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController   //обработчик http-запросов
@RequestMapping("/reservation")
public class ReservationController {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(ReservationController.class);
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable("id") Long id) {
        logger.info("Callded getReservationById: id=" + id);
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.getReservationById(id));
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        logger.info("Callded getAllReservations");
        return ResponseEntity.ok(reservationService.findAllReservation());
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody @Valid Reservation reservationToCreate) { //@RequestBody брать из тела запроса
        logger.info("Callded createReservation");
        return ResponseEntity.status(201).header("test-header", "123").body(reservationService.createReservation(reservationToCreate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable("id") Long id, @RequestBody Reservation reservationToUpdate) {
        logger.info("Callded updateReservation id:" + reservationToUpdate.id());
        return ResponseEntity.ok(reservationService.updateReservation(id, reservationToUpdate));
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable("id") Long id) {
        logger.info("Callded deleteReservation id: " + id);
        reservationService.cancelReservation(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveReservation(@PathVariable Long id) {
        logger.info("Callded approveReservation id: " + id);
        var reservation = reservationService.approveReservation(id);
        return ResponseEntity.ok(reservation);
    }
}
