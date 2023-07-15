package antifraud.Card;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
@Component
public class CardNumberValidator {
    public boolean isValidNumber(String cardNumber) {
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
}
