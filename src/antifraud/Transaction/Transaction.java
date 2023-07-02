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
    long id;
    @Column(name = "amount", nullable = false)
    @Positive
    long amount;
    @Column(name = "ip_address", nullable = false)
    String ip;
    @Column(name = "card_number", nullable = false)
    @CreditCardNumber
    String number;
    @Column(name = "region", nullable = false)
    @Pattern(regexp = "(EAP|ECA|HIC|LAC|MENA|SA)")
    Region region;
    @JsonProperty(value = "date")
    @Column(name = "date", nullable = false)
    LocalDateTime localDateTime;

}
