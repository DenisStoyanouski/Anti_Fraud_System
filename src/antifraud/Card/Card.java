package antifraud.Card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity
@Table(name="card")
public class Card {
    @Id
    @SequenceGenerator(
            name = "card_id_seq",
            sequenceName = "card_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "card_id_seq"
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    long id;

    @NotBlank
    @JsonProperty(required = true)
    @Pattern(regexp = "\\d{16}")
    private String number;
}
