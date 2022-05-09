package licence.code.generator.security.validation;


import licence.code.generator.dto.UserDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordComplianceChecker implements ConstraintValidator<MatchedPassword, Object> {

    @Override
    public void initialize(final MatchedPassword arg0) {

    }

    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
        final UserDto user = (UserDto) obj;
        return user.getPassword().equals(user.getMatchedPassword());
    }

}
