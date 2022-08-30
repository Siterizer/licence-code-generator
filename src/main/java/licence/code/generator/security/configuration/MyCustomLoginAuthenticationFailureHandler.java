package licence.code.generator.security.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("myAuthenticationFailureHandler")
public class MyCustomLoginAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException exception) throws IOException, ServletException {
        logger.debug("Failure login attempt from: {}", "idk");
        super.onAuthenticationFailure(request, response, exception);
    }
}