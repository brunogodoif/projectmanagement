package br.com.brunogodoif.projectmanagement.infrastructure.controllers;

import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityInUseException;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.response.ErrorResponse;
import br.com.brunogodoif.projectmanagement.infrastructure.gateways.exceptions.DatabaseOperationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex,
                                                                        HttpServletRequest request
                                                                       ) {
        log.error("Illegal argument exception: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                                                "Bad Request",
                                                ex.getMessage(),
                                                request.getRequestURI());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseOperationException(DatabaseOperationException ex,
                                                                          HttpServletRequest request
                                                                         ) {
        log.error("Database operation exception: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                "Internal Server Error",
                                                "An error occurred while processing your request",
                                                request.getRequestURI());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex,
                                                                     HttpServletRequest request
                                                                    ) {
        log.error("Access denied exception: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                                                "Forbidden",
                                                "You don't have permission to access this resource",
                                                request.getRequestURI());

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex,
                                                                    HttpServletRequest request
                                                                   ) {
        log.error("Validation exception: {}", ex.getMessage());

        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(fieldName + ": " + errorMessage);
        });

        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                                                "Validation Error",
                                                "Invalid input data",
                                                request.getRequestURI(),
                                                errors);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected exception: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                "Internal Server Error",
                                                "An unexpected error occurred",
                                                request.getRequestURI());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex,
                                                                       HttpServletRequest request
                                                                      ) {
        log.error("Entity not found exception: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                                                "Not Found",
                                                ex.getMessage(),
                                                request.getRequestURI());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityInUseException.class)
    public ResponseEntity<ErrorResponse> handleEntityInUseException(EntityInUseException ex,
                                                                    HttpServletRequest request
                                                                   ) {
        log.error("Entity in use exception: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(),
                                                "Conflict",
                                                ex.getMessage(),
                                                request.getRequestURI());

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
}

