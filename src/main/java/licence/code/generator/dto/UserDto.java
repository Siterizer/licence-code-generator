package licence.code.generator.dto;

import licence.code.generator.security.validation.ValidEmail;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserDto {
    @NotNull
    @Size(min = 3, message = "Length must be greater than {min}")
    private String username;

    @ValidEmail
    @NotNull
    @Size(min = 3, message = "Length must be greater than {min}")
    private String email;

    @NotNull
    @Size(min = 3)
    private String password;

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

    @Override
    public String toString() {
        return "UserDto [username=" +
                username +
                ", email=" +
                email + "]";
    }

}
