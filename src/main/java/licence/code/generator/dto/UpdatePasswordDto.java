package licence.code.generator.dto;

import jakarta.validation.constraints.NotNull;
import licence.code.generator.security.validation.MatchedUpdatePassword;
import licence.code.generator.security.validation.ValidPassword;

@MatchedUpdatePassword
public record UpdatePasswordDto(
        @NotNull String oldPassword,
        @ValidPassword String newPassword,
        @ValidPassword String newMatchedPassword) {

    @Override
    public String toString() {
        return "It is not possible to view passwords";
    }
}
