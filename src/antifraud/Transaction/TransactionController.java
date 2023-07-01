package antifraud.Transaction;

import antifraud.Card.CardRepository;
import antifraud.IpAddress.IpAddressRepository;
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
import java.util.Map;


@RestController
public class TransactionController {

    @Autowired
    CardRepository cardRepository;

    @Autowired
    IpAddressRepository ipAddressRepository;

    @PostMapping(path = "/api/antifraud/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity makeTransaction(@Valid @RequestBody Transaction transaction) {
        if (!CardNumberValidator.isValidNumber(transaction.getNumber()) ||
                !IpAddressValidator.isValidIpAddress(transaction.getIp())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Map<String, String> response;
        long amount = transaction.getAmount();
        if (amount > 0 && amount <= 200) {
            response = Map.of("result", Result.ALLOWED.name());
            return ResponseEntity.ok().body(response);
        } else if (amount > 200 && amount <= 1500) {
            response = Map.of("result", Result.MANUAL_PROCESSING.name());
            return ResponseEntity.ok().body(response);
        } else if (amount > 1500) {
            response = Map.of("result", Result.PROHIBITED.name());
            return ResponseEntity.ok().body(response);
        }
        return ResponseEntity.badRequest().body(HttpStatus.BAD_REQUEST);
    }

    private boolean isIpAddressInBlacklist(String ip) {
        return ipAddressRepository.existsByIp(ip);
    }

    private boolean isCardInBlacklist(String cardNumber) {
        return cardRepository.existsByNumber(cardNumber);
    }
}
