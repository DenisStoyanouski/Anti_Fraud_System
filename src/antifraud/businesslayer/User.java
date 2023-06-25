package antifraud.businesslayer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="_user")
public class User {
    @Id
    @GeneratedValue
    long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String username;
    @NotBlank
    @JsonProperty(required = true, access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String role; // should be prefixed with ROLE_
}
