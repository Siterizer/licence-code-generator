package licence.code.generator.dto.mapper;

import licence.code.generator.dto.UserDto;
import licence.code.generator.entities.Role;
import licence.code.generator.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class UserDtoMapper {
    public UserDto toDto(User user) {
        List<String> roles = user
                .getRoles()
                .stream()
                .map(Role::getName)
                .collect(toList());

        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), roles);
    }
}
