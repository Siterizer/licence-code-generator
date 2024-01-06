package licence.code.generator.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import licence.code.generator.security.validation.MatchedUserPassword;
import licence.code.generator.security.validation.ValidEmail;
import licence.code.generator.security.validation.ValidPassword;

@MatchedUserPassword
public record RegisterUserDto(
        @NotNull @Size(min = 3, message = "Length must be greater than {min}") String username,
        @ValidEmail String email,
        @ValidPassword String password,
        @ValidPassword String matchedPassword) {
    @Override
    public String toString() {
        return "RegisterUserDto [username=" +
                username +
                ", email=" +
                email + "]";
    }

}
