package licence.code.generator.security.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CustomLocaleResolver extends AcceptHeaderLocaleResolver {

    List<Locale> LOCALES = Arrays.asList(new Locale("en"), new Locale("pl"));

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        if (!StringUtils.hasLength(request.getHeader(HttpHeaders.ACCEPT_LANGUAGE))) {
            //return Locale.of("en-US");
            //In case if App will need to get default Locale from machine location:
            return Locale.getDefault();
        }
        List<Locale.LanguageRange> list = Locale.LanguageRange.parse(request.getHeader(HttpHeaders.ACCEPT_LANGUAGE));
        return Locale.lookup(list, LOCALES);
    }
}
