package antifraud.Transaction;
import antifraud.Card.Card;
import antifraud.Card.CardController;
import antifraud.Card.CardRepository;
import antifraud.Card.CardService;
import antifraud.IpAddress.IpAddressController;
import antifraud.IpAddress.IpAddressRepository;
import antifraud.IpAddress.IpAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class TransactionValidator {
    @Autowired
    IpAddressService ipAddressService;

    @Autowired
    CardService cardService;

    static String result;
    static List<String> info;

    public Map<String, String> getResult(Transaction transaction) {
        validateAmount(transaction);
        validateCardNumber(transaction);
        validateIpAddress(transaction);
        Map<String, String> validationResult = new LinkedHashMap<>();
        validationResult.put("result", result);
        validationResult.put("info", info.size() == 0 ? "none" : info.stream().sorted().collect(Collectors.joining(",")));
        return validationResult;
    }

    private void validateAmount(Transaction transaction) {
        long amount = transaction.getAmount();
        if (amount > 0 && amount <= 200) {
            result = Result.ALLOWED.name();
        } else if (amount > 200 && amount <= 1500) {
            result = Result.MANUAL_PROCESSING.name();
            info.add("amount");
        } else if (amount > 1500) {
            result = Result.PROHIBITED.name();
            info.add("amount");
        }
    }

    private void validateCardNumber(Transaction transaction) {
        if (cardService.existByNumber(transaction.getNumber())) {
            result = Result.PROHIBITED.name();
            info.add("card-number");
        }
    }

    private void validateIpAddress(Transaction transaction) {
        if (ipAddressService.existByIp(transaction.getIp())) {
            result = Result.PROHIBITED.name();
            info.add("ip");
        }
    }
}
