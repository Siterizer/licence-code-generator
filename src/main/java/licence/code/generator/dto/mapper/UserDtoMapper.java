package licence.code.generator.dto.mapper;

import licence.code.generator.dto.LicenceDto;
import licence.code.generator.dto.UserDto;
import licence.code.generator.entities.Role;
import licence.code.generator.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class UserDtoMapper {
    private final LicenceDtoMapper licenceDtoMapper;

    @Autowired
    UserDtoMapper(LicenceDtoMapper licenceDtoMapper){
        this.licenceDtoMapper = licenceDtoMapper;
    }

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    public UserDto toDto(User user) {
        LOGGER.debug("Mapping User: {} to UserDto", user);
        List<String> roles = user
                .getRoles()
                .stream()
                .map(Role::getName)
                .collect(toList());
        List<LicenceDto> licences = user.getLicences()
                .stream()
                .map(licenceDtoMapper::toDto)
                .collect(toList());

        UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), roles, licences, !user.isAccountNonLocked());
        LOGGER.debug("Mapped UserDto: {}", userDto);

        return userDto;
    }
}
