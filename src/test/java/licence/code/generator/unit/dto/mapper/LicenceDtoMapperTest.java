package licence.code.generator.unit.dto.mapper;

import licence.code.generator.dto.LicenceDto;
import licence.code.generator.dto.mapper.LicenceDtoMapper;
import licence.code.generator.entities.Licence;
import licence.code.generator.helper.JpaLicenceEntityHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
class LicenceDtoMapperTest {

    @Autowired
    JpaLicenceEntityHelper jpaLicenceEntityHelper;
    @Autowired
    LicenceDtoMapper dtoMapper;

    @Test
    public void toDto_shouldPerformMapping() {
        //given:
        Licence licence = jpaLicenceEntityHelper.createRandomLicence();

        //when:
        LicenceDto result = dtoMapper.toDto(licence);

        //then:
        assertEquals(licence.getProduct().getName(), result.name());
        assertEquals(licence.getId(), result.licence());
    }

    @Test
    public void toDto_shouldThrowNullPointerException() {
        //given:
        Licence licenceWithNulls = new Licence();
        Licence licenceWithoutProduct = new Licence();
        licenceWithoutProduct.setId("123");

        //when-then:
        assertThrows(NullPointerException.class, () -> dtoMapper.toDto(licenceWithNulls));
        assertThrows(NullPointerException.class, () -> dtoMapper.toDto(licenceWithoutProduct));
    }

}