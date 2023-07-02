package antifraud.Transaction;

import antifraud.businesslayer.CardNumberValidator;
import antifraud.businesslayer.IpAddressValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;


@RestController
public class TransactionController {

    @Autowired
    TransactionValidator transactionValidator;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    ControllerExceptionHandler controllerExceptionHandler;

    @PostMapping(path = "/api/antifraud/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<? extends Object> makeTransaction(@Valid @RequestBody Transaction transaction) {
        transactionRepository.save(transaction);
        return ResponseEntity.status(HttpStatus.OK).body(transactionValidator.getResult(transaction));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity handleDateTimeParseException(
            DateTimeParseException e,
            WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
