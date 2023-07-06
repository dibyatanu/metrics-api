package uk.claritygroup.advice;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import uk.claritygroup.exception.BadRequestException;

@Slf4j
@RestControllerAdvice
public final class MetricsServiceAdvice {
    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<String> notFound(final RuntimeException exception){
        log.error("INVALID_RESPONSE: "+ exception.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("{\"message\":\""+ exception.getMessage()+"\"}");
    }
    @ExceptionHandler(value = {org.hibernate.exception.ConstraintViolationException.class,
            jakarta.validation.ConstraintViolationException.class,WebExchangeBindException.class,Exception.class})
    public ResponseEntity<String> invalidRequest(final RuntimeException exception){
        log.error("INVALID_RESPONSE: "+ exception.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("{\"message\":\"A required parameter was not supplied or is invalid\"}");
    }
    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<String>  invalidUpdateRequest(final BadRequestException exception){
        log.error("INVALID_RESPONSE: "+ exception.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("{\"message\":\""+ exception.getMessage()+"\"}");
    }

}
