package antifraud.Transaction;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Transaction {
    @NotNull
    long amount;
    @NotBlank
    String ip;
    @NotBlank
    String number;

}
