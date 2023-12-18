package licence.code.generator.dto;

import licence.code.generator.security.validation.MatchedUserPassword;
import licence.code.generator.security.validation.ValidEmail;
import licence.code.generator.security.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@MatchedUserPassword
public class RegisterUserDto {
    @NotNull
    @Size(min = 3, message = "Length must be greater than {min}")
    private String username;

    @ValidEmail
    @NotNull
    @Size(min = 3, message = "Length must be greater than {min}")
    private String email;

    @NotNull
    @ValidPassword
    private String password;

    @NotNull
    @ValidPassword
    private String matchedPassword;

    @Override
    public String toString() {
        return "RegisterUserDto [username=" +
                username +
                ", email=" +
                email + "]";
    }

}
