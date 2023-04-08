package antifraud.businesslayer;

import javax.validation.constraints.NotNull;

public class Transaction {
    @NotNull
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
