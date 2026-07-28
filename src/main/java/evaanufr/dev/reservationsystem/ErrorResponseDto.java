package evaanufr.dev.reservationsystem;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        String msg,
        LocalDateTime timestamp,
        String errorMsg
) {
    
}
