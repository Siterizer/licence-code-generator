package licence.code.generator.security.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import licence.code.generator.dto.RegisterUserDto;

import java.util.Objects;

public class UserPasswordComplianceChecker implements ConstraintValidator<MatchedUserPassword, RegisterUserDto> {

    @Override
    public void initialize(final MatchedUserPassword arg0) {

    }

    @Override
    public boolean isValid(final RegisterUserDto user, final ConstraintValidatorContext context) {
        if (Objects.isNull(user.getPassword())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("password must not be null").addConstraintViolation();
            return false;
        } else if (Objects.isNull(user.getMatchedPassword())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("matchedPassword must not be null").addConstraintViolation();
            return false;
        }

        return user.getPassword().equals(user.getMatchedPassword());
    }

}
