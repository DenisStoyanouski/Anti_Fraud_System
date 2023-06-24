package antifraud.businesslayer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    private String name;
    private String username;
    @JsonProperty(required = true, access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    //private String role; // should be prefixed with ROLE_
}
