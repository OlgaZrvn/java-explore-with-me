package ru.yandex.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.info("Код ответа {}", HttpStatus.BAD_REQUEST);
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.info("Код ответа {}", HttpStatus.NOT_FOUND);
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus (code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalErrorException(final InternalErrorException e) {
        log.info("Код ответа {}", HttpStatus.INTERNAL_SERVER_ERROR);
        return new ErrorResponse(
                e.getMessage()
        );
    }
}
