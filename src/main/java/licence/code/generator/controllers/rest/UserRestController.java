package licence.code.generator.controllers.rest;

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

import javax.validation.Valid;
import java.util.Objects;

import static licence.code.generator.util.GeneratorStringUtils.USER_INFO_PATH;
import static licence.code.generator.util.GeneratorStringUtils.USER_UPDATE_PASSWORD_PATH;


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

    @RequestMapping(value = USER_INFO_PATH, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserDto> getCurrentUserDetails() {
        User requester = getRequester();
        LOGGER.info("Showing user info for user with id: {}", requester.getId());
        UserDto dto = userDtoMapper.toDto(requester);
        return ResponseEntity.ok(dto);
    }

    @PostMapping(value = {USER_UPDATE_PASSWORD_PATH})
    public ResponseEntity<?> updateUserPassword(@Valid @RequestBody final UpdatePasswordDto passwordDto) {
        User requester = getRequester();
        LOGGER.info("Changing password for user with id: {}", requester.getId());
        userService.changeUserPassword(requester, passwordDto.getOldPassword(), passwordDto.getNewPassword());
        LOGGER.info("Password changed for User with id: {}", requester.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/user/resetPassword")
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

