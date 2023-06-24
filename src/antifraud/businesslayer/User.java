package antifraud.businesslayer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    long id;
    @NotBlank
    private String name;
    @NotBlank
    private String username;
    @NotBlank
    @JsonProperty(required = true, access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String role; // should be prefixed with ROLE_
}
