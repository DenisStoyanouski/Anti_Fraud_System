package antifraud.businesslayer;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class Transaction {
    @NotNull
    @Min(0)
    //@Pattern(regexp = "\\d*")
    long amount;

    public Transaction() {
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
