package licence.code.generator.controllers.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import licence.code.generator.dto.RegisterUserDto;
import licence.code.generator.dto.UpdatePasswordDto;
import licence.code.generator.dto.UserDto;
import licence.code.generator.dto.mapper.UserDtoMapper;
import licence.code.generator.entities.User;
import licence.code.generator.services.IUserService;
import licence.code.generator.web.exception.UnauthorizedUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static licence.code.generator.util.GeneratorStringUtils.*;


@Tag(name = "User", description = "User Rest API")
@RestController
public class UserRestController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final IUserService userService;
    private final UserDtoMapper userDtoMapper;

    @Autowired
    public UserRestController(IUserService userService, UserDtoMapper userDtoMapper) {
        this.userService = userService;
        this.userDtoMapper = userDtoMapper;
    }

    @Operation(
            summary = "Create new User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "There is already an account with that username/email"),
    })
    @PostMapping(value = {REGISTER_PATH})
    public ResponseEntity<?> registerUser(@Valid @RequestBody final RegisterUserDto userDto) {
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
        System.out.println("siema");
        userService.confirmRegistration(token);
        LOGGER.info("Confirmed registration for user with Verification Token: {}", token);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Fetch User details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Requester is not logged-in")
    })
    @RequestMapping(value = USER_INFO_PATH, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserDto> getCurrentUserDetails() {
        User requester = getRequester();
        LOGGER.info("Showing user info for user with id: {}", requester.getId());
        UserDto dto = userDtoMapper.toDto(requester);
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "Update User password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Requester is not logged-in"),
            @ApiResponse(responseCode = "401", description = "old password is incorrect"),
    })
    @PostMapping(value = {USER_UPDATE_PASSWORD_PATH})
    public ResponseEntity<?> updateUserPassword(@Valid @RequestBody final UpdatePasswordDto passwordDto) {
        User requester = getRequester();
        LOGGER.info("Changing password for user with id: {}", requester.getId());
        userService.changeUserPassword(requester, passwordDto.oldPassword(), passwordDto.newPassword());
        LOGGER.info("Password changed for User with id: {}", requester.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping(value = {USER_RESET_PASSWORD_PATH})
    public String resetUserPassword() {
        //TODO create resetPassword functionality
        //userService.registerUser(user);
        return "users";
    }

    private User getRequester() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            //Note this is never going to happen in real-life usage case as Spring Security prevents any Unauthorized
            //entrance with RestAuthenticationEntryPoint class
            throw new UnauthorizedUserException("Unauthorized User wanted to access /user/ GET request");
        }
        return userService.findUserByUsername(authentication.getName());
    }
}

