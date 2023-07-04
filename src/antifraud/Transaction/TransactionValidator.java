package antifraud.Transaction;

import antifraud.Card.CardService;
import antifraud.IpAddress.IpAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionValidator {
    private final IpAddressService ipAddressService;
    private final CardService cardService;
    private final TransactionRepository transactionRepository;
    private String result;
    private List<String> info;
    private Transaction transaction;

    @Autowired
    public TransactionValidator(IpAddressService ipAddressService,
                                CardService cardService,
                                TransactionRepository transactionRepository) {
        this.ipAddressService = ipAddressService;
        this.cardService = cardService;
        this.transactionRepository = transactionRepository;

    }

    public Map<String, String> getResult(Transaction transaction) {
        this.transaction = transaction;
        info = new ArrayList<>();
        validateAmount();
        validateCardNumber();
        validateIpAddress();
        validateRegionLastHour();
        validateIpAddressLastHour();
        Map<String, String> validationResult = new LinkedHashMap<>();
        validationResult.put("result", result);
        validationResult.put("info", info.size() == 0 ? "none" : info.stream().sorted().collect(Collectors.joining(", ")));
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
        if (cardService.existByNumber(transaction.getNumber())) {
            if (!Objects.equals(result, Result.PROHIBITED.name())) {
                info.clear();
            }
            result = Result.PROHIBITED.name();
            info.add("card-number");
        }
    }

    private void validateIpAddress() {
        if (ipAddressService.existByIp(transaction.getIp())) {
            if (!Objects.equals(result, Result.PROHIBITED.name())) {
                info.clear();
            }
            result = Result.PROHIBITED.name();
            info.add("ip");
        }
    }

    private void validateRegionLastHour() {
        long numberOfRegions = transactionRepository.countTransactionsFromTwoAnotherRegionsInLastHour(
                transaction.getRegion().name(),
                transaction.getLocalDateTime().minus(1, ChronoUnit.HOURS),
                transaction.getLocalDateTime());

        if (numberOfRegions == 2 && !Objects.equals(result, Result.PROHIBITED.name())) {
            result = Result.MANUAL_PROCESSING.name();
            info.add("region-correlation");
        } else if (numberOfRegions > 2) {
            if (!Objects.equals(result, Result.PROHIBITED.name())) {
                result = Result.PROHIBITED.name();
                info.clear();
            }
            info.add("region-correlation");
        }
    }

    private void validateIpAddressLastHour() {
        long numberOfAddresses = transactionRepository.countTransactionsFromTwoAnotherIpAddressInLastHour(
                transaction.getIp(),
                transaction.getLocalDateTime().minus(1, ChronoUnit.HOURS),
                transaction.getLocalDateTime());
        if (numberOfAddresses == 2 && !Objects.equals(result, Result.PROHIBITED.name())) {
            result = Result.MANUAL_PROCESSING.name();
            info.add("ip-correlation");
        } else if (numberOfAddresses > 2) {
            if (!Objects.equals(result, Result.PROHIBITED.name())) {
                result = Result.PROHIBITED.name();
                info.clear();
            }
            info.add("ip-correlation");
        }
    }
}
