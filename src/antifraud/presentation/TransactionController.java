package antifraud.presentation;

import antifraud.businesslayer.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;


@RestController
public class TransactionController {

    @PostMapping(path = "/api/antifraud/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity makeTransaction(@Valid @RequestBody Transaction transaction) {
        Map<String, String> response;
        long amount = transaction.getAmount();
        if (amount > 0 && amount <= 200) {
            response = Map.of("result", "ALLOWED");
            return ResponseEntity.ok().body(response);
        } else if (amount > 200 && amount <= 1500) {
            response = Map.of("result", "MANUAL_PROCESSING");
            return ResponseEntity.ok().body(response);
        } else if (amount > 1500) {
            response = Map.of("result", "PROHIBITED");
            return ResponseEntity.ok().body(response);
        }
        return ResponseEntity.badRequest().body(HttpStatus.BAD_REQUEST);
    }
}
