package licence.code.generator.unit.dto;

import licence.code.generator.dto.UpdatePasswordDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UpdatePasswordDtoValidationTest {
    private static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @ParameterizedTest
    @MethodSource("provideUpdatePasswordDtoValuesWithViolationNumber")
    public void shouldValidate(UpdatePasswordDto dto, int violationNumber){
        //when:
        Set<ConstraintViolation<UpdatePasswordDto>> validateResultSet = validator.validate(dto);

        //then:
        assertEquals(violationNumber, validateResultSet.size());
    }

    private static Stream<Arguments> provideUpdatePasswordDtoValuesWithViolationNumber() {
        return Stream.of(
                //oldPassword:
                Arguments.of(new UpdatePasswordDto(null, null, null), 4),
                Arguments.of(new UpdatePasswordDto("test", null, null), 3),
                //newPassword:
                Arguments.of(new UpdatePasswordDto("", null, null), 3),
                Arguments.of(new UpdatePasswordDto("", null, null), 3),
                Arguments.of(new UpdatePasswordDto("", "abcde", null), 3),
                Arguments.of(new UpdatePasswordDto("", "abcdef", null), 3),
                Arguments.of(new UpdatePasswordDto("", "abcdefg", null), 3),
                Arguments.of(new UpdatePasswordDto("", "abcdefgh", null), 3),
                Arguments.of(new UpdatePasswordDto("", "1", null), 3),
                Arguments.of(new UpdatePasswordDto("", "12", null), 3),
                Arguments.of(new UpdatePasswordDto("", "123", null), 3),
                Arguments.of(new UpdatePasswordDto("", "1234", null), 3),
                Arguments.of(new UpdatePasswordDto("", "12345", null), 3),
                Arguments.of(new UpdatePasswordDto("", "123456", null), 2),
                //newMatchedPassword:
                Arguments.of(new UpdatePasswordDto("", "abcdef6", "abcde"), 2),
                Arguments.of(new UpdatePasswordDto("", "abcdef6", "abcdef"), 2),
                Arguments.of(new UpdatePasswordDto("", "abcdef6", "abcdefg"), 2),
                Arguments.of(new UpdatePasswordDto("", "abcdef6", "abcdefgh"), 2),
                Arguments.of(new UpdatePasswordDto("", "abcdef6", "1"), 2),
                Arguments.of(new UpdatePasswordDto("", "abcdef6", "12"), 2),
                Arguments.of(new UpdatePasswordDto("", "abcdef6", "123"), 2),
                Arguments.of(new UpdatePasswordDto("", "abcdef6", "1234"), 2),
                Arguments.of(new UpdatePasswordDto("", "abcdef6", "12345"), 2),
                Arguments.of(new UpdatePasswordDto("", "abcdef6", "123456"), 1),
                Arguments.of(new UpdatePasswordDto("", "abcdef6", "abcdef61"), 1),
                Arguments.of(new UpdatePasswordDto("", "abcdef16", "abcdef61"), 1),
                Arguments.of(new UpdatePasswordDto("", "abcdef6", "abcdef6"), 0)
        );
    }

}