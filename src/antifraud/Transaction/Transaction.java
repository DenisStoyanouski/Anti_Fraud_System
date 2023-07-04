package antifraud.Transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import org.hibernate.validator.constraints.CreditCardNumber;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "transaction")

@JsonPropertyOrder({
        "transactionId",
        "amount",
        "ip",
        "number",
        "region",
        "date",
        "result",
        "feedback"
})
public class Transaction {
    @Id
    @SequenceGenerator(
            name = "transaction_id_seq",
            sequenceName = "transaction_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "transaction_id_seq"
    )
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
    @Enumerated(EnumType.STRING)
    Region region;
    @JsonProperty(value = "date")
    @Column(name = "date", nullable = false)
    LocalDateTime localDateTime;
    @Column(name = "result")
    String result;
    @Column(name = "feedback")
    String feedback = "";
}
