package licence.code.generator.dto;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserDto {
    private final Long id;
    private final String username;
    private final String email;
    private final List<String> roles;
    private final boolean isLocked;


    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public List<String> getRoles() {
        return roles;
    }

    public boolean isLocked() {
        return isLocked;
    }

    @Override
    public String toString() {
        return "UserDto [username=" +
                username +
                ", id=" +
                id +
                ", isLocked=" +
                isLocked +
                ", email=" +
                email + "]";
    }
}
