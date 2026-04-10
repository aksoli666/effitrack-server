package com.effitrack.server.handler;

import com.effitrack.server.constant.StringConst;
import com.effitrack.server.model.AppError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<AppError> handleNotFound(NoSuchElementException e) {
        return new ResponseEntity<>(
                new AppError(HttpStatus.NOT_FOUND.value(), StringConst.ERROR_PREFIX_OBJ_NOT_FOUND + e.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<AppError> handleBadCredentials(BadCredentialsException e) {
        return new ResponseEntity<>(
                new AppError(HttpStatus.UNAUTHORIZED.value(), StringConst.ERROR_MSG_BAD_CREDENTIALS),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AppError> handleIllegalArgument(IllegalArgumentException e) {
        return new ResponseEntity<>(
                new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AppError> handleGlobalException(Exception e) {
        return new ResponseEntity<>(
                new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), StringConst.ERROR_PREFIX_SERVER + e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
