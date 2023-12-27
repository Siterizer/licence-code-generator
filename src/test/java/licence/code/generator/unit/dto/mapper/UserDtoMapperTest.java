package licence.code.generator.unit.dto.mapper;

import licence.code.generator.dto.LicenceDto;
import licence.code.generator.dto.UserDto;
import licence.code.generator.dto.mapper.UserDtoMapper;
import licence.code.generator.entities.Licence;
import licence.code.generator.entities.RoleName;
import licence.code.generator.entities.User;
import licence.code.generator.helper.JpaLicenceEntityHelper;
import licence.code.generator.helper.JpaUserEntityHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserDtoMapperTest {

    @Autowired
    UserDtoMapper dtoMapper;
    @Autowired
    JpaUserEntityHelper jpaUserEntityHelper;
    @Autowired
    JpaLicenceEntityHelper jpaLicenceEntityHelper;


    @Test
    public void toDto_shouldPerformMapping() {
        //given:
        User user = jpaUserEntityHelper.createRandomUser();
        Licence licence1 = jpaLicenceEntityHelper.addRandomLicenceToExistingUser(user);
        Licence licence2 = jpaLicenceEntityHelper.addRandomLicenceToExistingUser(user);
        List<String> licenceIds = List.of(licence1.getId(), licence2.getId());
        List<String> licenceNames = List.of(licence1.getProduct().getName(), licence2.getProduct().getName());

        //when:
        UserDto result = dtoMapper.toDto(user);
        List<String> collectedLicences = result.getLicences().stream().map(LicenceDto::getLicence).collect(Collectors.toList());
        List<String> collectedLicenceNames = result.getLicences().stream().map(LicenceDto::getName).collect(Collectors.toList());

        //then:
        assertEquals(result.getId(), user.getId());
        assertEquals(result.getUsername(), user.getUsername());
        assertEquals(result.getEmail(), user.getEmail());
        assertEquals(result.isLocked(), user.isLocked());
        assertTrue(result.getRoles().contains(RoleName.ROLE_USER.name()));
        collectedLicences.containsAll(licenceIds);
        collectedLicenceNames.containsAll(licenceNames);
    }

    @Test
    public void toDto_shouldThrowNullPointerException() {
        //given:
        User user = null;

        //when:
        assertThrows(NullPointerException.class, () -> dtoMapper.toDto(user));

    }

}