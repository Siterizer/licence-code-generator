package licence.code.generator.security.validation;


import licence.code.generator.dto.RegisterUserDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserPasswordComplianceChecker implements ConstraintValidator<MatchedUserPassword, RegisterUserDto> {

    @Override
    public void initialize(final MatchedUserPassword arg0) {

    }

    @Override
    public boolean isValid(final RegisterUserDto user, final ConstraintValidatorContext context) {
        return user.getPassword().equals(user.getMatchedPassword());
    }

}
