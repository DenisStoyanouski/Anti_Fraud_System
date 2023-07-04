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
    private List<String> info = new ArrayList<>();
    private Transaction transaction;

    private long maxAllowed = 200;
    private long maxManualProcessing = 1500;

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
        validateAmount();
        validateCardNumber();
        validateIpAddress();
        validateRegionLastHour();
        validateIpAddressLastHour();
        return formResult();
    }

    private void updateLimits() {

    }

    private void validateAmount() {
        long amount = transaction.getAmount();
        if (amount > 0 && amount <= maxAllowed) {
            result = Result.ALLOWED.name();
        } else if (amount > maxAllowed && amount <= maxManualProcessing) {
            result = Result.MANUAL_PROCESSING.name();
            info.add("amount");
        } else if (amount > maxManualProcessing) {
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

    private Map<String, String> formResult() {
        Map<String, String> validationResult = new LinkedHashMap<>();
        validationResult.put("result", result);
        validationResult.put("info", info.size() == 0 ? "none" : info.stream().sorted().collect(Collectors.joining(", ")));
        return validationResult;
    }
}
