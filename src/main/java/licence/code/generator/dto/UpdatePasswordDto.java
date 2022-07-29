package licence.code.generator.dto;

import licence.code.generator.security.validation.MatchedUpdatePassword;
import licence.code.generator.security.validation.ValidPassword;

import javax.validation.constraints.NotNull;

@MatchedUpdatePassword
public class UpdatePasswordDto {

    @NotNull
    private String oldPassword;

    @NotNull
    @ValidPassword
    private String newPassword;

    @NotNull
    @ValidPassword
    private String newMatchedPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewMatchedPassword() {
        return newMatchedPassword;
    }

    public void setNewMatchedPassword(String newMatchedPassword) {
        this.newMatchedPassword = newMatchedPassword;
    }

    @Override
    public String toString() {
        return "It is not possible to view passwords";
    }
}
