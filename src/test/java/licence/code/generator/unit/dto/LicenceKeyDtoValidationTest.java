package licence.code.generator.unit.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import licence.code.generator.dto.LicenceKeyDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LicenceKeyDtoValidationTest {
    private static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @ParameterizedTest
    @MethodSource("provideLicenceKeyDtoValuesWithViolationNumber")
    public void shouldValidate(LicenceKeyDto dto, int violationNumber) {
        //when:
        Set<ConstraintViolation<LicenceKeyDto>> validateResultSet = validator.validate(dto);

        //then:
        assertEquals(violationNumber, validateResultSet.size());
    }

    private static Stream<Arguments> provideLicenceKeyDtoValuesWithViolationNumber() {
        return Stream.of(
                Arguments.of(new LicenceKeyDto(""), 1),
                Arguments.of(new LicenceKeyDto(null), 1),
                Arguments.of(new LicenceKeyDto("c49acq44"), 1),
                Arguments.of(new LicenceKeyDto("c49acq44-b281"), 1),
                Arguments.of(new LicenceKeyDto("c49acq44-b281-4944"), 1),
                Arguments.of(new LicenceKeyDto("c49acq44-b281-4944-a1c8"), 1),
                Arguments.of(new LicenceKeyDto("a4a69970-72a1-4f0b-b324-0f30a76fe372"), 0)
        );
    }

}