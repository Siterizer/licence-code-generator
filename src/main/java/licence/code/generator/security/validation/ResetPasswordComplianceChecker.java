package licence.code.generator.security.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import licence.code.generator.dto.ResetPasswordDto;

import java.util.Objects;

public class ResetPasswordComplianceChecker implements ConstraintValidator<MatchedResetPassword, ResetPasswordDto> {

    @Override
    public void initialize(final MatchedResetPassword arg0) {

    }

    @Override
    public boolean isValid(final ResetPasswordDto passwordDto, final ConstraintValidatorContext context) {
        if (Objects.isNull(passwordDto.newPassword()) || Objects.isNull(passwordDto.newMatchedPassword())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("field must not be null").addConstraintViolation();
            return false;
        }
        return passwordDto.newPassword().equals(passwordDto.newMatchedPassword());
    }

}
