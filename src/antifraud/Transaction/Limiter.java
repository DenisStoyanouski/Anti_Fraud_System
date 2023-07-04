package antifraud.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class Limiter {
    final private TransactionRepository transactionRepository;

    private String number;

    @Autowired
    Limiter(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getMaxAllowedLimit() {
        long currentLimit = 200;
        List<Transaction> transactions = transactionRepository.findAllByFeedbackAndNumber(Result.ALLOWED.name(), number, Sort.by("id"));
        if (transactions.size() != 0) {
            for (var transaction : transactions) {
                currentLimit = (long) Math.ceil(0.8 * currentLimit + 0.2 * transaction.getAmount());
            }
        }
        return currentLimit;
    }

    public long getMaxManualLimit() {
        long currentLimit = 1500;
        List<Transaction> transactions = transactionRepository.findAllByFeedbackAndNumber(Result.PROHIBITED.name(), number, Sort.by("id"));
        if (transactions.size() != 0) {
            for (var transaction : transactions) {
                currentLimit = (long) Math.ceil(0.8 * currentLimit - 0.2 * transaction.getAmount());
            }
        }
        return currentLimit;
    }
}
