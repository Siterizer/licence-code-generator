package licence.code.generator.security.configuration;

import licence.code.generator.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component("myAuthenticationSuccessHandler")
public class MyCustomLoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException {
        final HttpSession session = request.getSession(false);
        final String userName = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        if (session != null) {
            session.setMaxInactiveInterval(30 * 60);
            logger.debug("Setting session maxInactiveInterval for user with name: {}", userName);
        }
        logger.debug("Successful login attempt from user with username : {}", userName);
    }
}