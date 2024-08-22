package licence.code.generator.dto;

import licence.code.generator.security.validation.MatchedResetPassword;
import licence.code.generator.security.validation.ValidPassword;

@MatchedResetPassword
public record ResetPasswordDto(
        @ValidPassword String newPassword,
        @ValidPassword String newMatchedPassword) {

    @Override
    public String toString() {
        return "It is not possible to view passwords";
    }
}
