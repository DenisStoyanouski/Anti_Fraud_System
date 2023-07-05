package antifraud.Limit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name="limit")
public class Limit {
    @Id()
    @SequenceGenerator(
            name = "limit_id_seq",
            sequenceName = "limit_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "limit_id_seq"
    )
    long id;
    @Column(unique = true, nullable = false)
    private String number;
    @Column(nullable = false)
    private long maxAllowed;
    @Column(nullable = false)
    private long maxManual;
}
