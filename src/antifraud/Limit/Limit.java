package antifraud.Limit;

import lombok.*;

import javax.persistence.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "amount_limit")
public class Limit {
    @Id
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

    @Column(name = "number", unique = true, nullable = false)
    private String number;

    @Column(name = "max_allowed_limit",nullable = false)
    private long maxAllowed;

    @Column(name = "max_manual_limit", nullable = false)
    private long maxManual;
}
