package licence.code.generator.controllers.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import licence.code.generator.dto.ResetPasswordDto;
import licence.code.generator.dto.UsernameDto;
import licence.code.generator.dto.mapper.UserDtoMapper;
import licence.code.generator.services.user.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static licence.code.generator.util.GeneratorStringUtils.*;

@Tag(name = "Reset password", description = "Reset password rest API")
@RestController
@RequestMapping(API_PATH)
public class ResetPasswordRestController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final IUserService userService;

    @Autowired
    public ResetPasswordRestController(IUserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Reset User password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful operation"),
            @ApiResponse(responseCode = "201", description = "Unable to find User for given username (note we do not want" +
                    "to know the requester if the username exists and email was sent. That's why it produces 201 Code)"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping(value = {USER_SEND_RESET_PASSWORD_EMAIL_PATH})
    public ResponseEntity<?> resetUserPasswordEmail(@Valid @RequestBody UsernameDto usernameDto) {
        LOGGER.info("Resetting password for user with username: {}", usernameDto);
        userService.createResetUserPasswordToken(usernameDto.username());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Change password using token and new password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Verification Token does not exists"),
            @ApiResponse(responseCode = "410", description = "Verification Token has expired")
    })
    @PostMapping(value = {USER_RESET_PASSWORD_PATH})
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token, @Valid @RequestBody ResetPasswordDto passwordDto) {
        LOGGER.info("Changing password for user with Verification Token: {}", token);
        userService.resetUserPassword(token, passwordDto.newPassword());
        LOGGER.info("Changed password for user with Verification Token: {}", token);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
