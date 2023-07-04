package antifraud.Transaction;

import antifraud.businesslayer.CardNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.time.format.DateTimeParseException;
import java.util.List;
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

    @Autowired
    CardNumberValidator cardNumberValidator;


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
    public ResponseEntity<Object> addFeedback(@Valid @RequestBody Feedback feedback) {
        if (transactionRepository.existsById(feedback.transactionId())) {
            if (Objects.equals(transactionRepository.findById(feedback.transactionId()).get().getResult(), feedback.feedback().name())) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
            }
            if (Objects.equals(transactionRepository.findById(feedback.transactionId()).get().getFeedback(), feedback.feedback().name()) ||
            !"".equals(transactionRepository.findById(feedback.transactionId()).get().getFeedback())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            Transaction transaction = transactionRepository.findById(feedback.transactionId()).get();
            transaction.setFeedback(feedback.feedback().name());
            transactionRepository.save(transaction);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(transactionRepository.findById(feedback.transactionId()));
    }

    @GetMapping(path = "/history")
    public List<Transaction> findAll() {
        return transactionRepository.findAll(Sort.by("id"));
    }

    @GetMapping(path = "/history/{number}")
    public ResponseEntity<Object> findByCardNumber(@PathVariable String number) {
        if (!cardNumberValidator.isValidNumber(number)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        List<Transaction> result = transactionRepository.findAllByNumber(number, Sort.by("id"));
        if (result.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().body(result);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<Object> handleDateTimeParseException(
            DateTimeParseException e,
            WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e,
            WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
