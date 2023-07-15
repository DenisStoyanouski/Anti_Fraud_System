package antifraud.Exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            DataIntegrityViolationException.class,
            ConstraintViolationException.class,
            IllegalStateException.class
    })
    public ResponseEntity<Object> handleAllException(RuntimeException e) {
        return ResponseEntity.badRequest().build();
    }

}
