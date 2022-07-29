package licence.code.generator.security.validation;


import licence.code.generator.dto.UpdatePasswordDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UpdatePasswordComplianceChecker implements ConstraintValidator<MatchedUpdatePassword, UpdatePasswordDto> {

    @Override
    public void initialize(final MatchedUpdatePassword arg0) {

    }

    @Override
    public boolean isValid(final UpdatePasswordDto passwordDto, final ConstraintValidatorContext context) {
        return passwordDto.getNewPassword().equals(passwordDto.getNewMatchedPassword());
    }

}
