package licence.code.generator.unit.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import licence.code.generator.dto.RegisterUserDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RegisterUserDtoValidationTest {
    private static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @ParameterizedTest
    @MethodSource("provideRegisterUserDtoValuesWithViolationNumber")
    public void shouldValidate(RegisterUserDto dto, int violationNumber) {
        //when:
        Set<ConstraintViolation<RegisterUserDto>> validateResultSet = validator.validate(dto);

        //then:
        assertEquals(violationNumber, validateResultSet.size());
    }

    private static Stream<Arguments> provideRegisterUserDtoValuesWithViolationNumber() {
        return Stream.of(
                //username:
                Arguments.of(new RegisterUserDto(null, null, null, null), 5),
                Arguments.of(new RegisterUserDto("", null, null, null), 5),
                Arguments.of(new RegisterUserDto("12", null, null, null), 5),
                Arguments.of(new RegisterUserDto("123", null, null, null), 4),
                //email:
                Arguments.of(new RegisterUserDto("123", "", null, null), 4),
                Arguments.of(new RegisterUserDto("123", "asd123", null, null), 4),
                Arguments.of(new RegisterUserDto("123", "asd123.", null, null), 4),
                Arguments.of(new RegisterUserDto("123", "asd123.pl", null, null), 4),
                Arguments.of(new RegisterUserDto("123", "asd123.com", null, null), 4),
                Arguments.of(new RegisterUserDto("123", "@asd123.com", null, null), 4),
                Arguments.of(new RegisterUserDto("123", "a@asd123", null, null), 4),
                Arguments.of(new RegisterUserDto("123", "a@asd123.", null, null), 4),
                Arguments.of(new RegisterUserDto("123", "a@asd123.com", null, null), 3),
                //password:
                Arguments.of(new RegisterUserDto("123", "a@a.aa", null, null), 3),
                Arguments.of(new RegisterUserDto("123", "a@a.aa", "abcde", null), 3),
                Arguments.of(new RegisterUserDto("123", "a@a.aa", "abcdef", null), 3),
                Arguments.of(new RegisterUserDto("123", "a@a.aa", "abcdefg", null), 3),
                Arguments.of(new RegisterUserDto("123", "a@a.aa", "abcdefgh", null), 3),
                Arguments.of(new RegisterUserDto("123", "a@a.aa", "1", null), 3),
                Arguments.of(new RegisterUserDto("123", "a@a.aa", "12", null), 3),
                Arguments.of(new RegisterUserDto("123", "a@a.aa", "123", null), 3),
                Arguments.of(new RegisterUserDto("123", "a@a.aa", "1234", null), 3),
                Arguments.of(new RegisterUserDto("123", "a@a.aa", "12345", null), 3),
                //matchedPassword:
                Arguments.of(new RegisterUserDto("123", "a@a.aa", "123456", null), 2),
                Arguments.of(new RegisterUserDto("123", "a@a.aa", "abcdef6", "abcde"), 2),
                Arguments.of(new RegisterUserDto("123", "a@a.aa", "abcdef6", "abcdef"), 2),
                Arguments.of(new RegisterUserDto("123", "a@a.aa", "abcdef6", "abcdefg"), 2),
                Arguments.of(new RegisterUserDto("123", "a@a.aa", "abcdef6", "abcdefgh"), 2),
                Arguments.of(new RegisterUserDto("123", "a@a.aa", "abcdef6", "1"), 2),
                Arguments.of(new RegisterUserDto("123", "a@a.aa", "abcdef6", "12"), 2),
                Arguments.of(new RegisterUserDto("123", "a@a.aa", "abcdef6", "123"), 2),
                Arguments.of(new RegisterUserDto("123", "a@a.aa", "abcdef6", "1234"), 2),
                Arguments.of(new RegisterUserDto("123", "a@a.aa", "abcdef6", "12345"), 2),
                Arguments.of(new RegisterUserDto("123", "a@a.aa", "abcdef6", "123456"), 1),
                //@MatchedUserPassword
                Arguments.of(new RegisterUserDto("123", "a@a.aa", "abcdef6", "abcdef61"), 1),
                Arguments.of(new RegisterUserDto("123", "a@a.aa", "abcdef16", "abcdef61"), 1),
                Arguments.of(new RegisterUserDto("123", "a@a.aa", "abcdef6", "abcdef6"), 0)
        );
    }

}