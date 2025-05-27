package sora.com.saleapi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String error,
        String errorType,
        String message,
        String path,
        LocalDateTime timestamp
) {
    public ErrorResponse(HttpStatus status, String message, String path, String errorType) {
        this(
                status.value(),
                status.getReasonPhrase(),
                errorType,
                message,
                path,
                LocalDateTime.now()
        );
    }
}