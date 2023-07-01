package antifraud.Transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


@Data
@AllArgsConstructor
@Getter
@Setter
public class Transaction {
    @NotBlank
    @Pattern(regexp = "\\d*")
    long amount;
    @NotBlank
    String ip;
    @NotBlank
    String number;

}
