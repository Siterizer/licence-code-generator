package licence.code.generator.unit.security.validation;

import licence.code.generator.security.validation.EmailValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintValidatorContext;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
class EmailValidatorTest {
    @Mock
    private ConstraintValidatorContext constraintValidatorContext;
    private static EmailValidator emailValidator;

    @BeforeAll
    public static void init() {
        emailValidator = new EmailValidator();
    }


    @ParameterizedTest
    @MethodSource("provideEmailsWithValidationResult")
    public void isValid_shouldValidate(String email, boolean expectedResult) {
        //when:
        boolean result = emailValidator.isValid(email, constraintValidatorContext);

        //then:
        assertEquals(expectedResult, result);
    }

    private static Stream<Arguments> provideEmailsWithValidationResult() {
        return Stream.of(
                Arguments.of("asd123", false),
                Arguments.of("asd123.", false),
                Arguments.of("asd123.pl", false),
                Arguments.of("asd123.com", false),
                Arguments.of("asd123.com", false),
                Arguments.of("@asd123.com", false),
                Arguments.of("a@asd123", false),
                Arguments.of("a@asd123.", false),
                Arguments.of("a@asd123.com", true),
                Arguments.of("a@asd123.pl", true)
        );
    }
}