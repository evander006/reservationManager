package evaanufr.dev.reservationsystem.web;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        String msg,
        LocalDateTime timestamp,
        String errorMsg
) {

}
