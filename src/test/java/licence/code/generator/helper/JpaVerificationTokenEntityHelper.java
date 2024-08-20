package licence.code.generator.helper;

import licence.code.generator.entities.User;
import licence.code.generator.entities.VerificationToken;
import licence.code.generator.repositories.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class JpaVerificationTokenEntityHelper {
    @Autowired
    private VerificationTokenRepository repository;
    @Autowired
    private JpaUserEntityHelper userEntityHelper;

    public VerificationToken expireVerificationToken(VerificationToken verificationToken) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.DATE, -1);
        verificationToken.setExpiryDate(cal.getTime());
        repository.save(verificationToken);
        return verificationToken;

    }

    public VerificationToken createExpiredVerificationToken(User user) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, -10);
        if (user == null) {
            user = userEntityHelper.createExpireddUser();
        }
        VerificationToken result = new VerificationToken(user);
        result.setExpiryDate(new Date(cal.getTime().getTime()));
        repository.save(result);
        return result;
    }
}
