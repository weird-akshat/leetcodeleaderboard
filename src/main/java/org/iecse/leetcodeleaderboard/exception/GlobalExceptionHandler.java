package org.iecse.leetcodeleaderboard.exception;



import lombok.extern.slf4j.Slf4j;
import org.iecse.leetcodeleaderboard.dto.ErrorResponse;
import org.iecse.leetcodeleaderboard.security.exception.InvalidOTPException;
import org.iecse.leetcodeleaderboard.security.exception.LeetcodeVerificationFailedException;
import org.iecse.leetcodeleaderboard.security.exception.UserAlreadyExistsException;
import org.iecse.leetcodeleaderboard.security.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            UserProfileNotFoundException.class,
            UserNotFoundException.class
    })
    public Mono<ResponseEntity<ErrorResponse>> handleNotFound(RuntimeException ex) {
        log.warn("Not Found: {}", ex.getMessage());
        return Mono.just(ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, "Not Found", ex.getMessage())));
    }

    @ExceptionHandler({
            LeetcodeIdNotVerifiedException.class,
            LeetcodeIdUpdateException.class,
            LeetcodeIdChangedException.class,
            InvalidOTPException.class,
            LeetcodeVerificationFailedException.class
    })
    public Mono<ResponseEntity<ErrorResponse>> handleBadRequest(RuntimeException ex) {
        log.warn("Bad Request: {}", ex.getMessage());
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, "Bad Request", ex.getMessage())));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleConflict(UserAlreadyExistsException ex) {
        log.warn("Conflict: {}", ex.getMessage());
        return Mono.just(ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(409, "Conflict", ex.getMessage())));
    }

    @ExceptionHandler(LeetcodeAPIException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleExternalAPIError(LeetcodeAPIException ex) {
        log.error("External API Error: {}", ex.getMessage(), ex);
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse(502, "Bad Gateway", "Error communicating with LeetCode servers")));
    }

    @ExceptionHandler(DatabaseOperationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDatabaseError(DatabaseOperationException ex) {
        log.error("Database Error: {}", ex.getMessage(), ex);
        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, "Internal Server Error", "A database operation failed")));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex) {
        log.error("Unhandled Exception: {}", ex.getMessage(), ex);
        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, "Internal Server Error", "An unexpected error occurred")));
    }
}