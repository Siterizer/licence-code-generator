package licence.code.generator.dto;

import licence.code.generator.security.validation.MatchedUserPassword;
import licence.code.generator.security.validation.ValidEmail;
import licence.code.generator.security.validation.ValidPassword;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchedPassword() {
        return matchedPassword;
    }

    public void setMatchedPassword(String matchedPassword) {
        this.matchedPassword = matchedPassword;
    }

    @Override
    public String toString() {
        return "RegisterUserDto [username=" +
                username +
                ", email=" +
                email + "]";
    }

}
