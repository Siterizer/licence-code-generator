package licence.code.generator.security.validation;


import licence.code.generator.dto.UserDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserPasswordComplianceChecker implements ConstraintValidator<MatchedUserPassword, UserDto> {

    @Override
    public void initialize(final MatchedUserPassword arg0) {

    }

    @Override
    public boolean isValid(final UserDto user, final ConstraintValidatorContext context) {
        return user.getPassword().equals(user.getMatchedPassword());
    }

}
