package antifraud.businesslayer;

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
@Table(name="_user")
public class User {
    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    long id;
    @NotBlank
    private String name;
    @NotBlank
    private String username;
    @NotBlank
    @JsonProperty(required = true, access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Enumerated(EnumType.STRING)
    private Role role; // should be prefixed with ROLE_
}
