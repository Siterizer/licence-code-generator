package licence.code.generator.unit.dto.mapper;

import licence.code.generator.dto.UserDto;
import licence.code.generator.dto.mapper.UserDtoMapper;
import licence.code.generator.entities.RoleName;
import licence.code.generator.entities.User;
import licence.code.generator.helper.JpaUserEntityHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserDtoMapperTest {

    @Autowired
    UserDtoMapper dtoMapper;
    @Autowired
    JpaUserEntityHelper jpaUserEntityHelper;

    @Test
    public void toDto_shouldPerformMapping() {
        //given:
        User user = jpaUserEntityHelper.createRandomUser();

        //when:
        UserDto result = dtoMapper.toDto(user);

        //then:
        assertEquals(user.getId(), result.id());
        assertEquals(user.getUsername(), result.username());
        assertEquals(user.getEmail(), result.email());
        assertEquals(user.isLocked(), result.isLocked());
        assertEquals(user.isAccountExpired(), result.isExpired());
        assertTrue(result.roles().contains(RoleName.ROLE_USER.name()));
    }

    @Test
    public void toDto_shouldThrowNullPointerException() {
        //given:
        User user = null;

        //when:
        assertThrows(NullPointerException.class, () -> dtoMapper.toDto(user));

    }

}