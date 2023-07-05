package antifraud.Transaction;

import antifraud.Card.CardService;
import antifraud.IpAddress.IpAddressService;
import antifraud.Limit.Limit;
import antifraud.Limit.LimitService;
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

    private final LimitService limitService;
    private Result result;
    private List<String> info;
    private Transaction transaction;

    @Autowired
    public TransactionValidator(IpAddressService ipAddressService,
                                CardService cardService,
                                TransactionRepository transactionRepository,
                                LimitService limitService)
    {
        this.ipAddressService = ipAddressService;
        this.cardService = cardService;
        this.transactionRepository = transactionRepository;
        this.limitService = limitService;
    }

    public Map<String, String> getResult(Transaction transaction) {
        this.transaction = transaction;
        info = new ArrayList<>();
        validateAmount();
        validateCardNumber();
        validateIpAddress();
        validateRegionLastHour();
        validateIpAddressLastHour();
        return formResult();
    }

    private void validateAmount() {
        long amount = transaction.getAmount();
        long maxAllowed = limitService.getMaxAllowedLimitByNumber(transaction.getNumber());
        long maxManual = limitService.getMaxManualLimitByNumber(transaction.getNumber());

        if (amount > 0 && amount <= maxAllowed) {
            result = Result.ALLOWED;
        } else if (amount > maxAllowed && amount <= maxManual) {
            result = Result.MANUAL_PROCESSING;
            info.add("amount");
        } else if (amount > maxManual) {
            result = Result.PROHIBITED;
            info.add("amount");
        }
    }

    private void validateCardNumber() {
        if (cardService.existByNumber(transaction.getNumber())) {
            if (!Objects.equals(result, Result.PROHIBITED)) {
                info.clear();
            }
            result = Result.PROHIBITED;
            info.add("card-number");
        }
    }

    private void validateIpAddress() {
        if (ipAddressService.existByIp(transaction.getIp())) {
            if (!Objects.equals(result, Result.PROHIBITED)) {
                info.clear();
            }
            result = Result.PROHIBITED;
            info.add("ip");
        }
    }

    private void validateRegionLastHour() {
        long numberOfRegions = transactionRepository.countTransactionsFromTwoAnotherRegionsInLastHour(
                transaction.getRegion().name(),
                transaction.getLocalDateTime().minus(1, ChronoUnit.HOURS),
                transaction.getLocalDateTime());

        if (numberOfRegions == 2 && !Objects.equals(result, Result.PROHIBITED)) {
            result = Result.MANUAL_PROCESSING;
            info.add("region-correlation");
        } else if (numberOfRegions > 2) {
            if (!Objects.equals(result, Result.PROHIBITED)) {
                result = Result.PROHIBITED;
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
        if (numberOfAddresses == 2 && !Objects.equals(result, Result.PROHIBITED)) {
            result = Result.MANUAL_PROCESSING;
            info.add("ip-correlation");
        } else if (numberOfAddresses > 2) {
            if (!Objects.equals(result, Result.PROHIBITED)) {
                result = Result.PROHIBITED;
                info.clear();
            }
            info.add("ip-correlation");
        }
    }

    private Map<String, String> formResult() {
        Map<String, String> validationResult = new LinkedHashMap<>();
        validationResult.put("result", result.name());
        validationResult.put("info", info.size() == 0 ? "none" : info.stream().sorted().collect(Collectors.joining(", ")));
        return validationResult;
    }
}
