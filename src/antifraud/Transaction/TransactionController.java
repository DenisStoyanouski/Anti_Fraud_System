package antifraud.Transaction;

import antifraud.businesslayer.CardNumberValidator;
import antifraud.businesslayer.IpAddressValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
public class TransactionController {

    @Autowired
    TransactionValidator transactionValidator;

    @PostMapping(path = "/api/antifraud/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<? extends Object> makeTransaction(@Valid @RequestBody Transaction transaction) {
        if (!CardNumberValidator.isValidNumber(transaction.getNumber()) ||
                !IpAddressValidator.isValidIpAddress(transaction.getIp())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(transactionValidator.getResult(transaction));
    }


}
