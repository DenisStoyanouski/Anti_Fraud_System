package antifraud.Transaction;

import antifraud.businesslayer.CardNumberValidator;
import antifraud.businesslayer.IpAddressValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;


@RestController
public class TransactionController {

    @Autowired
    TransactionValidator transactionValidator;

    @Autowired
    TransactionRepository transactionRepository;

    @PostMapping(path = "/api/antifraud/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<? extends Object> makeTransaction(@Valid @RequestBody Transaction transaction) {
        if (!CardNumberValidator.isValidNumber(transaction.getNumber()) ||
                !IpAddressValidator.isValidIpAddress(transaction.getIp()) ||
                transaction.getAmount() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        transactionRepository.save(transaction);
        return ResponseEntity.status(HttpStatus.OK).body(transactionValidator.getResult(transaction));
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            IllegalStateException.class
    })
    public ResponseEntity<Object> handleMethodArgumentAndViolation(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }


}
