package licence.code.generator.dto;

import licence.code.generator.security.validation.MatchedUpdatePassword;
import licence.code.generator.security.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@MatchedUpdatePassword
public class UpdatePasswordDto {

    @NotNull
    private String oldPassword;

    @ValidPassword
    private String newPassword;

    @ValidPassword
    private String newMatchedPassword;

    @Override
    public String toString() {
        return "It is not possible to view passwords";
    }
}
