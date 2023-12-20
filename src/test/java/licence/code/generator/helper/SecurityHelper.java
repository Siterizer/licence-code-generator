package licence.code.generator.helper;

import licence.code.generator.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityHelper {

    @Autowired
    JpaUserEntityHelper jpaUserEntityHelper;

    public void setSecurityContextFromUser(User user) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
    }

    public void setSecurityContextFromRandomUser() {
        User user = jpaUserEntityHelper.createNotBlockedUser();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
    }

    public void setSecurityContextFromRandomAdmin() {
        User admin = jpaUserEntityHelper.createNotBlockedAdmin();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(admin.getUsername(), admin.getPassword()));
    }
}
