package licence.code.generator.security.validation;


import licence.code.generator.dto.UpdatePasswordDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class UpdatePasswordComplianceChecker implements ConstraintValidator<MatchedUpdatePassword, UpdatePasswordDto> {

    @Override
    public void initialize(final MatchedUpdatePassword arg0) {

    }

    @Override
    public boolean isValid(final UpdatePasswordDto passwordDto, final ConstraintValidatorContext context) {
        if(Objects.isNull(passwordDto.getNewPassword()) || Objects.isNull(passwordDto.getNewMatchedPassword())){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("field must not be null").addConstraintViolation();
            return false;
        }
        return passwordDto.getNewPassword().equals(passwordDto.getNewMatchedPassword());
    }

}
