package antifraud.Transaction;
import antifraud.Card.CardRepository;
import antifraud.IpAddress.IpAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransactionValidator {
    @Autowired
    CardRepository cardRepository;

    @Autowired
    IpAddressRepository ipAddressRepository;
    Transaction transaction;
    String result;
    List<String> info;

    TransactionValidator(Transaction transaction) {
        this.transaction = transaction;
    }

    public Map<String, String> getResult() {
        validateAmount();
        validateCardNumber();
        validateIpAddress();
        Map<String, String> validationResult = new LinkedHashMap<>();
        validationResult.put("result", result);
        validationResult.put("info", info.size() == 0 ? "none" : info.stream().sorted().collect(Collectors.joining(",")));
        return validationResult;
    }

    private void validateAmount() {
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

    private void validateCardNumber() {
        if (cardRepository.existsByNumber(transaction.getNumber())) {
            result = Result.PROHIBITED.name();
            info.add("card-number");
        }
    }

    private void validateIpAddress() {
        if (ipAddressRepository.existsByIp(transaction.getIp())) {
            result = Result.PROHIBITED.name();
            info.add("ip");
        }
    }
}
