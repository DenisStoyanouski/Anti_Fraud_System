package antifraud.Transaction;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public record Feedback(
        @NotNull
        long transactionId,
        @Enumerated(EnumType.STRING)
        Result feedback) {}
