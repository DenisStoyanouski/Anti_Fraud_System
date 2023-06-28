package antifraud.businesslayer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleChanger {
    @NotBlank
    String username;
    @Pattern(regexp = "(SUPPORT|MERCHANT)")
    String role;
}
