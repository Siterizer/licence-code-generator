package licence.code.generator.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class UserDto {
    private final Long id;
    private final String username;
    private final String email;
    private final List<String> roles;
    private final List<LicenceDto> licences;
    private final boolean isLocked;

    @Override
    public String toString() {
        return "UserDto [username=" +
                username +
                ", id=" +
                id +
                ", isLocked=" +
                isLocked +
                ", roles=" +
                roles +
                ", licences=" +
                licences +
                ", email=" +
                email + "]";
    }
}
