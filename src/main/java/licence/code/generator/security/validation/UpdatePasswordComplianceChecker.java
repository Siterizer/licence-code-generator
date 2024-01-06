package licence.code.generator.security.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import licence.code.generator.dto.UpdatePasswordDto;

import java.util.Objects;

public class UpdatePasswordComplianceChecker implements ConstraintValidator<MatchedUpdatePassword, UpdatePasswordDto> {

    @Override
    public void initialize(final MatchedUpdatePassword arg0) {

    }

    @Override
    public boolean isValid(final UpdatePasswordDto passwordDto, final ConstraintValidatorContext context) {
        if (Objects.isNull(passwordDto.newPassword()) || Objects.isNull(passwordDto.newMatchedPassword())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("field must not be null").addConstraintViolation();
            return false;
        }
        return passwordDto.newPassword().equals(passwordDto.newMatchedPassword());
    }

}
