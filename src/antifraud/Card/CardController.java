package antifraud.Card;

import antifraud.IpAddress.IpAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class CardController {

    @Autowired
    CardRepository cardRepository;

    @PostMapping("/api/antifraud/stolencard")
    public ResponseEntity<Card> addCard(@RequestBody Card card) {
        if (!isValidNumber(card.getNumber())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (cardRepository.existsByNumber(card.getNumber())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        cardRepository.save(card);
        return ResponseEntity.status(HttpStatus.CREATED).body(card);
    }

    @DeleteMapping("/api/antifraud/stolencard/{number}")
    public ResponseEntity removeCard(@PathVariable String number) {
        if (!isValidNumber(number)) {
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

    private boolean isValidNumber (String cardNumber) {
        ArrayList<Integer> number = Arrays.stream(cardNumber.split(""))
                .map(Integer::parseInt).collect(Collectors.toCollection(ArrayList::new));
        // The Luhn algorithm
        // Multiply odd indexes by 2
        for (int i = 0; i < number.size(); i += 2) {
            number.set(i, number.get(i) * 2);
        }
        // Subtract 9 to numbers over 9
        for (int i = 0; i < number.size(); i++) {
            if (number.get(i) > 9) {
                number.set(i, number.get(i) - 9);
            }
        }
        // Add all numbers
        int sum = number.stream().mapToInt(Integer::intValue).sum();
        // If the received number is divisible by 10 with the remainder equal to zero, then this number is valid;
        // otherwise, the card number is not valid.
        return sum % 10 == 0;
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handler(
            MethodArgumentNotValidException e, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}


