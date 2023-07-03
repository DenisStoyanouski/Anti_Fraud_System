package antifraud.Transaction;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public record Feedback(@NotNull long transactionId,
                       @Pattern(regexp = "(ALLOWED|MANUAL_PROCESSING|PROHIBITED)") String feedback) {
}
