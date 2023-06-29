package antifraud.businesslayer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLocker {
    @NotBlank
    String username;
    @Pattern(regexp = "(LOCK|UNLOCK)")
    String operation;
}
