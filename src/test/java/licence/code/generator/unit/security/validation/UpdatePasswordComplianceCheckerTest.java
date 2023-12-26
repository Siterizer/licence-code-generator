package licence.code.generator.unit.security.validation;

import licence.code.generator.dto.UpdatePasswordDto;
import licence.code.generator.security.validation.UpdatePasswordComplianceChecker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintValidatorContext;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class UpdatePasswordComplianceCheckerTest {
    @Mock
    private ConstraintValidatorContext constraintValidatorContext;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;
    private static UpdatePasswordComplianceChecker passwordValidator;

    @BeforeAll
    public static void init() {
        passwordValidator = new UpdatePasswordComplianceChecker();
    }


    @ParameterizedTest
    @MethodSource("provideUpdatePasswordDtoWithValidationResult")
    public void isValid_shouldValidate(UpdatePasswordDto dto, boolean expectedResult) {
        //given:
        when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(constraintViolationBuilder);
        //when:
        boolean result = passwordValidator.isValid(dto, constraintValidatorContext);

        //then:
        assertEquals(expectedResult, result);
    }

    private static Stream<Arguments> provideUpdatePasswordDtoWithValidationResult() {
        return Stream.of(
                Arguments.of(new UpdatePasswordDto("old", "123", "321"), false),
                Arguments.of(new UpdatePasswordDto("old", null, "123"), false),
                Arguments.of(new UpdatePasswordDto("old", "123", null), false),
                Arguments.of(new UpdatePasswordDto("old", null, null), false),
                Arguments.of(new UpdatePasswordDto("old", "123", "123"), true)
        );
    }
}