package licence.code.generator.security.validation;

import com.google.common.base.Joiner;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;

import java.util.Arrays;
import java.util.Objects;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(final ValidPassword arg0) {

    }

    @Override
    public boolean isValid(final String password, final ConstraintValidatorContext context) {
        if (Objects.isNull(password)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("must not be null").addConstraintViolation();
            return false;
        }
        final PasswordValidator validator = new PasswordValidator(Arrays.asList(
                new LengthRule(6, 30),
                new DigitCharacterRule(1),
                //TODO to be added when I care about password difficulty
                //new UppercaseCharacterRule(1),
                //new SpecialCharacterRule(1),
                //new NumericalSequenceRule(3,false),
                //new AlphabeticalSequenceRule(3,false),
                //new QwertySequenceRule(3,false),
                new WhitespaceRule()));
        final RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(Joiner.on(",").join(validator.getMessages(result))).addConstraintViolation();
        return false;
    }

}
