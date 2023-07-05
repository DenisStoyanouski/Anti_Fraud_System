package antifraud.IpAddress;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity
@Table(name = "ip_address")
public class IpAddress {
    @Id
    @SequenceGenerator(
            name = "ip_address_id_seq",
            sequenceName = "ip_address_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ip_address_id_seq"
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    long id;

    @NotBlank
    @JsonProperty(required = true)
    @Pattern(regexp = "^((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(?!$)|$)){4}$")
    private String ip;
}
