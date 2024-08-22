package licence.code.generator.controllers.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import licence.code.generator.dto.UpdatePasswordDto;
import licence.code.generator.dto.UserDto;
import licence.code.generator.dto.mapper.UserDtoMapper;
import licence.code.generator.entities.User;
import licence.code.generator.services.user.IUserService;
import licence.code.generator.web.exception.UnauthorizedUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static licence.code.generator.util.GeneratorStringUtils.*;


@Tag(name = "User", description = "User Rest API")
@RestController
@RequestMapping(API_PATH)
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
            summary = "Fetch User details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Requester is not logged-in")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @RequestMapping(value = USER_INFO_PATH, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserDto> getCurrentUserDetails() {
        User requester = getRequester();
        LOGGER.info("Showing user info for user with id: {}", requester.getId());
        UserDto dto = userDtoMapper.toDto(userService.loadUserWithRelatedEntitiesByUsername(requester.getUsername()));
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
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping(value = {USER_UPDATE_PASSWORD_PATH})
    public ResponseEntity<?> updateUserPassword(@Valid @RequestBody UpdatePasswordDto passwordDto) {
        User requester = getRequester();
        LOGGER.info("Changing password for user with id: {}", requester.getId());
        userService.changeUserPassword(requester.getId(), passwordDto.oldPassword(), passwordDto.newPassword());
        LOGGER.info("Password changed for User with id: {}", requester.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private User getRequester() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            //Note this is never going to happen in real-life usage case as Spring Security prevents any Unauthorized
            //entrance with RestAuthenticationEntryPoint class
            throw new UnauthorizedUserException("Unauthorized User wanted to access /user/ GET request");
        }
        return (User) authentication.getPrincipal();
    }
}

