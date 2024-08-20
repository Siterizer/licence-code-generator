package licence.code.generator.helper;

import licence.code.generator.entities.ResetPasswordToken;
import licence.code.generator.entities.User;
import licence.code.generator.repositories.ResetPasswordTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class JpaResetPasswordEntityHelper {
    @Autowired
    private ResetPasswordTokenRepository repository;
    @Autowired
    private JpaUserEntityHelper userEntityHelper;

    public ResetPasswordToken createResetPasswordToken(User user) {
        if (user == null) {
            user = userEntityHelper.createNotBlockedUser();
        }
        ResetPasswordToken result = new ResetPasswordToken(user);
        repository.save(result);
        return result;

    }

    public ResetPasswordToken createExpiredResetPasswordToken(User user) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, -10);
        if (user == null) {
            user = userEntityHelper.createNotBlockedUser();
        }
        ResetPasswordToken result = new ResetPasswordToken(user);
        result.setExpiryDate(new Date(cal.getTime().getTime()));
        repository.save(result);
        return result;
    }
}
