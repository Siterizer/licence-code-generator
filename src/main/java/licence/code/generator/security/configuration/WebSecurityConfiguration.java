package licence.code.generator.security.configuration;

import licence.code.generator.security.jwt.AuthTokenFilter;
import licence.code.generator.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.LocaleResolver;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfiguration {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RestAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.
                csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
                .authenticationProvider(authProvider())
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admin*").authenticated()
                        .requestMatchers("/api/licence*", "/api/products*").authenticated()
                        .requestMatchers("/api/user*").authenticated()
                        .requestMatchers("/api/swagger-ui/index.html*", "/api/v3/api-docs/swagger-config").authenticated()
                        .requestMatchers("/api/login*", "/api/register*", "/api/registrationConfirm*").permitAll()
                        .requestMatchers("/api/user/sendResetPasswordEmail", "/api/user/resetPassword").permitAll()
                        .requestMatchers("/css/*", "/js/*", "/favicon.ico", "/mainPage", "/", "/navbar").permitAll()
                        .requestMatchers("/admin", "/login", "/register", "/user").permitAll()
                        .requestMatchers("/api/licence/accordanceCheck*").permitAll()
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                )
                .build();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public LocaleResolver sessionLocaleResolver() {
        return new CustomLocaleResolver();
    }
}
