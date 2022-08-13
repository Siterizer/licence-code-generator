package licence.code.generator.dto.mapper;

import licence.code.generator.dto.UserEmailDto;
import licence.code.generator.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserEmailDtoMapper {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    public UserEmailDto toDto(User user) {
        LOGGER.debug("Mapping User: {} to UserEmailDto", user);

        UserEmailDto userDto = new UserEmailDto(user.getId(), user.getEmail());
        LOGGER.debug("Mapped UserEmailDto: {}", userDto);

        return userDto;
    }
}
