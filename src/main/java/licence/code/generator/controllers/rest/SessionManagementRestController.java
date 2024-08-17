package licence.code.generator.controllers.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import licence.code.generator.dto.LoginDto;
import licence.code.generator.dto.RegisterUserDto;
import licence.code.generator.entities.User;
import licence.code.generator.security.jwt.JwtUtils;
import licence.code.generator.services.user.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static licence.code.generator.util.GeneratorStringUtils.*;

@Tag(name = "Session", description = "Session Rest API")
@RestController
@RequestMapping(API_PATH)
public class SessionManagementRestController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final AuthenticationManager authenticationManager;
    private final IUserService userService;
    private final JwtUtils jwtUtils;

    @Autowired
    public SessionManagementRestController(IUserService userService, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @Operation(
            summary = "Login in using credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Bad credentials"),
    })
    @PostMapping(value = {LOGIN_PATH})
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User userDetails = (User) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        LOGGER.debug("Setting jwt cookie for user with: {} username", loginDto.username());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).build();
    }

    @Operation(
            summary = "Create new User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "There is already an account with that username/email"),
    })
    @PostMapping(value = {REGISTER_PATH})
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUserDto userDto) {
        LOGGER.info("Registering user with information: {}", userDto);
        userService.registerUser(userDto);
        LOGGER.info("User: {} registered", userDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Confirm registration using token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Verification Token does not exists"),
            @ApiResponse(responseCode = "409", description = "User email is already confirmed"),
            @ApiResponse(responseCode = "410", description = "Verification Token has expired")
    })
    @RequestMapping(value = REGISTRATION_CONFIRM_PATH, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> registrationConfirm(@RequestParam("token") String token) {
        LOGGER.info("Confirming registration for user with Verification Token: {}", token);
        userService.confirmRegistration(token);
        LOGGER.info("Confirmed registration for user with Verification Token: {}", token);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }
}
