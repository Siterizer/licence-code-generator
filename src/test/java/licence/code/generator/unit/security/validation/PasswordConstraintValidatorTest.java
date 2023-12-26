package licence.code.generator.unit.security.validation;

import licence.code.generator.security.validation.PasswordConstraintValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintValidatorContext;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class PasswordConstraintValidatorTest {
    @Mock
    private ConstraintValidatorContext constraintValidatorContext;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;
    private static PasswordConstraintValidator passwordValidator;

    @BeforeAll
    public static void init() {
        passwordValidator = new PasswordConstraintValidator();
    }


    @ParameterizedTest
    @MethodSource("providePasswordsWithValidationResult")
    public void isValid_shouldValidate(String password, boolean expectedResult) {
        //given:
        when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(constraintViolationBuilder);

        //when:
        boolean result = passwordValidator.isValid(password, constraintValidatorContext);

        //then:
        assertEquals(expectedResult, result);
    }

    private static Stream<Arguments> providePasswordsWithValidationResult() {
        return Stream.of(
                Arguments.of("abcde", false),
                Arguments.of("abcdef", false),
                Arguments.of("abcdefg", false),
                Arguments.of("abcdefgh", false),
                Arguments.of("abcdefgh", false),
                Arguments.of("1", false),
                Arguments.of("12", false),
                Arguments.of("123", false),
                Arguments.of("1234", false),
                Arguments.of("12345", false),
                Arguments.of("123456", true),
                Arguments.of("abcdef6", true)
        );
    }
}