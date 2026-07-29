package evaanufr.dev.reservationsystem.reservations;

public record ReservationFilter(
        Long roomId,
        Long userId,
        Integer pageSize,
        Integer pageNumber
) {
}
