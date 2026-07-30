package evaanufr.dev.reservationsystem.reservations.availability;

import ch.qos.logback.classic.Logger;
import evaanufr.dev.reservationsystem.reservations.ReservationController;
import evaanufr.dev.reservationsystem.reservations.ReservationEntity;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservation/availability")
public class ReservationAvailabilityController {
    private final ReservationAvailabilityService service;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(ReservationAvailabilityController.class);

    public ReservationAvailabilityController(ReservationAvailabilityService service) {
        this.service = service;
    }

    @PostMapping("/check")
    public ResponseEntity<CheckAvailabilityResponse> checkAvailability(
            @Valid @RequestBody CheckAvailabilityRequest request
    ) {
        logger.info("Called checkAvailability: request={}", request);
        boolean ifAvailable = service.checkIfAvailable(request.roomId(), request.startDate(), request.endDate());
        var msg = ifAvailable ? "Room is available for reservation" : "Room isn't available for reservation";
        var status = ifAvailable ? AvailabilityStatus.AVAILABLE : AvailabilityStatus.RESERVED;
        return ResponseEntity.status(200).body(new CheckAvailabilityResponse(msg, status));
    }
}
