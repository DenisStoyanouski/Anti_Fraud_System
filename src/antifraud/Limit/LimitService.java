package antifraud.Limit;

import antifraud.Transaction.Result;
import antifraud.Transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service

public class LimitService {
    private final LimitRepository limitRepository;

    @Autowired
    public LimitService(LimitRepository limitRepository) {
        this.limitRepository = limitRepository;
    }

    Optional<Limit> findByNumber(String number) {
        return limitRepository.findByNumber(number);
    };

    public boolean existsByNumber(String number) {
        return limitRepository.existByNumber(number);
    };

    public void saveLimit(Limit limit) {
        limitRepository.save(limit);
    }

    public void changeLimit(Transaction transaction) {
        Result result = transaction.getResult();
        Result feedback = transaction.getFeedback();
        String number = transaction.getNumber();
        Limit currentLimit = limitRepository.findByNumber(number).get();
        long amount = transaction.getAmount();
        if (result.equals(Result.ALLOWED)) {
            if (Result.MANUAL_PROCESSING.equals(feedback)) {
                currentLimit.setMaxAllowed(decreaseLimit(currentLimit.getMaxAllowed(), amount));
            }
            if (Result.PROHIBITED.equals(feedback)) {
                currentLimit.setMaxAllowed(decreaseLimit(currentLimit.getMaxAllowed(), amount));
                currentLimit.setMaxManual(decreaseLimit(currentLimit.getMaxManual(), amount));

            }
        }
    }

    private long increaseLimit(long currentMaxLimit, long currentAmount) {
        return (long) Math.ceil(0.8 * currentMaxLimit + 0.2 * currentAmount);
    }

    private long decreaseLimit(long currentMaxLimit, long currentAmount) {
        return (long) Math.ceil(0.8 * currentMaxLimit - 0.2 * currentAmount);
    }

}
