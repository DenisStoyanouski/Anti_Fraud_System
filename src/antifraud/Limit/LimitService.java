package antifraud.Limit;

import antifraud.Transaction.Result;
import antifraud.Transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service

public class LimitService {
    private final LimitRepository limitRepository;

    @Autowired
    public LimitService(LimitRepository limitRepository) {
        this.limitRepository = limitRepository;
    }

    public Optional<Limit> findByNumber(String number) {
        return limitRepository.findByNumber(number);
    }

    public boolean existsByNumber(String number) {
        return limitRepository.existByNumber(number);
    };
    @Transactional
    public void saveLimit(Limit limit) {
        limitRepository.save(limit);
    }

    public void changeLimit(Transaction transaction) {
        Result result = transaction.getResult();
        Result feedback = transaction.getFeedback();
        String number = transaction.getNumber();
        Limit currentLimit = limitRepository.findByNumber(number).get();
        long currentAllowedLimit = currentLimit.getMaxAllowed();
        long currentManualLimit = currentLimit.getMaxManual();
        long amount = transaction.getAmount();

        if (result.equals(Result.ALLOWED)) {
            if (feedback.equals(Result.MANUAL_PROCESSING)) {
                currentLimit.setMaxAllowed(decreaseLimit(currentAllowedLimit, amount));
            } else if (feedback.equals(Result.PROHIBITED)) {
                currentLimit.setMaxAllowed(decreaseLimit(currentAllowedLimit, amount));
                currentLimit.setMaxManual(decreaseLimit(currentManualLimit, amount));

            }
        }
        if (result.equals(Result.MANUAL_PROCESSING)) {
            if (Result.ALLOWED.equals(feedback)) {
                currentLimit.setMaxManual(increaseLimit(currentManualLimit, amount));
            } else if (Result.PROHIBITED.equals(feedback)){
                currentLimit.setMaxManual(decreaseLimit(currentManualLimit, amount));
            }
        }
        if (result.equals(Result.PROHIBITED)) {
            if (feedback.equals(Result.ALLOWED)) {
                currentLimit.setMaxAllowed(increaseLimit(currentAllowedLimit, amount));
                currentLimit.setMaxManual(increaseLimit(currentManualLimit, amount));
            } else if (feedback.equals(Result.MANUAL_PROCESSING)) {
                currentLimit.setMaxManual(increaseLimit(currentManualLimit, amount));
            }
        }
        saveLimit(currentLimit);
    }

    private long increaseLimit(long currentMaxLimit, long currentAmount) {
        return (long) Math.ceil(0.8 * currentMaxLimit + 0.2 * currentAmount);
    }

    private long decreaseLimit(long currentMaxLimit, long currentAmount) {
        return (long) Math.ceil(0.8 * currentMaxLimit - 0.2 * currentAmount);
    }

}
