package antifraud.Transaction;

import antifraud.Card.CardService;
import antifraud.IpAddress.IpAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionValidator {
    private final IpAddressService ipAddressService;
    private final CardService cardService;
    private String result;
    private List<String> info;

    @Autowired
    public TransactionValidator(IpAddressService ipAddressService, CardService cardService) {
        this.ipAddressService = ipAddressService;
        this.cardService = cardService;
    }

    public Map<String, String> getResult(Transaction transaction) {
        info = new ArrayList<>();
        validateAmount(transaction);
        validateCardNumber(transaction);
        validateIpAddress(transaction);
        Map<String, String> validationResult = new LinkedHashMap<>();
        validationResult.put("result", result);
        validationResult.put("info", info.size() == 0 ? "none" : info.stream().sorted().collect(Collectors.joining(", ")));
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
            if (Objects.equals(result, Result.MANUAL_PROCESSING.name())) {
                info.remove("amount");
            }
            result = Result.PROHIBITED.name();
            info.add("card-number");
        }
    }

    private void validateIpAddress(Transaction transaction) {
        if (ipAddressService.existByIp(transaction.getIp())) {
            if (Objects.equals(result, Result.MANUAL_PROCESSING.name())) {
                info.remove("amount");
            }
            result = Result.PROHIBITED.name();
            info.add("ip");
        }
    }
}
