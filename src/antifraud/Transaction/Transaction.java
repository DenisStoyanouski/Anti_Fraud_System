package antifraud.Transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.CreditCardNumber;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue
    @JsonProperty(value = "transactionId", access = JsonProperty.Access.READ_ONLY)
    long id;
    @Column(name = "amount", nullable = false)
    @Positive
    long amount;
    @Column(name = "ip_address", nullable = false)
    @Pattern(regexp = "^((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(?!$)|$)){4}$")
    String ip;
    @Column(name = "card_number", nullable = false)
    @CreditCardNumber
    String number;
    @Column(name = "region", nullable = false)
    @Pattern(regexp = "(EAP|ECA|HIC|LAC|MENA|SA|SSA)")
    String region;
    @JsonProperty(value = "date")
    @Column(name = "date", nullable = false)
    LocalDateTime localDateTime;
    @Column(name = "result")
    String result;
    @Column(name = "feedback")
    String feedback = "";

}
