package licence.code.generator.controllers.rest;

import licence.code.generator.dto.UpdatePasswordDto;
import licence.code.generator.dto.UserDto;
import licence.code.generator.dto.mapper.UserDtoMapper;
import licence.code.generator.entities.User;
import licence.code.generator.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
public class UserRestController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    @Autowired
    public UserRestController(UserService userService, UserDtoMapper userDtoMapper) {
        this.userService = userService;
        this.userDtoMapper = userDtoMapper;
    }

    @RequestMapping(value = "/user/info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserDto> getCurrentUserEmail() {
        final User user = userService.findUserByUsername(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        LOGGER.info("Showing user info for user with id: {}", user.getId());
        UserDto dto = userDtoMapper.toDto(user);


        return ResponseEntity.ok(dto);
    }

    @PostMapping(value = {"/user/updatePassword"})
    public ResponseEntity<?> updateUserPassword(@Valid final UpdatePasswordDto passwordDto) {
        final User user = userService.findUserByUsername(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        LOGGER.info("Changing password for user with id: {}", user.getId());
        userService.changeUserPassword(user, passwordDto.getOldPassword(), passwordDto.getNewPassword());
        LOGGER.info("Password changed for User with id: {}", user.getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/user/resetPassword")
    public String resetUserPassword() {
        //TODO create resetPassword functionality
        //userService.registerUser(user);
        return "users";
    }
}

