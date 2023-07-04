package antifraud.Transaction;

import antifraud.businesslayer.CardNumberValidator;
import antifraud.businesslayer.IpAddressValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Objects;


@RestController
@RequestMapping("/api/antifraud")
public class TransactionController {

    @Autowired
    TransactionValidator transactionValidator;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    ControllerExceptionHandler controllerExceptionHandler;

    @PostMapping(path = "/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<? extends Object> makeTransaction(@Valid @RequestBody Transaction transaction) {
        Map<String, String> validatorResult = transactionValidator.getResult(transaction);
        transaction.setResult(validatorResult.get("result"));
        transactionRepository.save(transaction);
        return ResponseEntity.status(HttpStatus.OK).body(validatorResult);
    }

    @PutMapping(path = "/transaction")
    @ResponseBody
    public ResponseEntity addFeedback(@Valid @RequestBody Feedback feedback) {
        if (transactionRepository.existsById(feedback.transactionId())) {
            if (Objects.equals(transactionRepository.findById(feedback.transactionId()).get().getResult(), feedback.feedback())) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
            }
            if (Objects.equals(transactionRepository.findById(feedback.transactionId()).get().getFeedback(), feedback.feedback())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            transactionRepository.findById(feedback.transactionId()).get().setFeedback(feedback.feedback());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(transactionRepository.findById(feedback.transactionId()));
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
