package com.capfer.hotel.booking.exceptions;

import com.capfer.hotel.booking.dtos.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // This will catch any exceptions that aren't handled by more specific handlers.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> handleAllUnknownExceptions(Exception ex) {
        ResponseDTO response = ResponseDTO.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseDTO> handleNotFoundExceptions(NotFoundException ex) {
        ResponseDTO response = ResponseDTO.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NameValueRequiredException.class)
    public ResponseEntity<ResponseDTO> handleNameValueRequiredException(NameValueRequiredException ex) {
        ResponseDTO response = ResponseDTO.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<ResponseDTO> handleInvalidCredentialException(InvalidCredentialException ex) {
        ResponseDTO response = ResponseDTO.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(InvalidBookingStateAndDateException.class)
    public ResponseEntity<ResponseDTO> handleInvalidBookingStateAndDateException(InvalidBookingStateAndDateException ex) {
        ResponseDTO response = ResponseDTO.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_GATEWAY);
    }

    // This is a catch-all for Spring Security's AccessDeniedException,
    // which is thrown when a user tries to access a resource they don't have permissions for.
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ProblemDetail handleSecurityAccessDenied(AccessDeniedException ex) {
        // You can wrap it in your better-named exception if you prefer
        var customEx = new InsufficientAuthorizationException("Admin rights required");
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, customEx.getMessage());
    }

}
