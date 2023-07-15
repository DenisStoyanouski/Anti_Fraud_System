package antifraud.Card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class CardController {
    @Autowired
    CardRepository cardRepository;

    @Autowired
    CardNumberValidator cardNumberValidator;

    @PostMapping("/api/antifraud/stolencard")
    public ResponseEntity<Card> addCard(@Valid @RequestBody Card card) {
        if (!cardNumberValidator.isValidNumber(card.getNumber())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (cardRepository.existsByNumber(card.getNumber())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        cardRepository.save(card);
        return ResponseEntity.status(HttpStatus.OK).body(card);
    }

    @DeleteMapping("/api/antifraud/stolencard/{number}")
    public ResponseEntity removeCard(@PathVariable String number) {
        if (!cardNumberValidator.isValidNumber(number)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (!cardRepository.existsByNumber(number)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        cardRepository.deleteByNumber(number);
        String status = String.format("Card %s successfully removed!", number);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", status));
    }

    @GetMapping("/api/antifraud/stolencard")
    public Iterable<Card> getAllCards() {
        return cardRepository.findAll();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handler(
            MethodArgumentNotValidException e, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}


