package licence.code.generator.controllers;

import licence.code.generator.dto.UpdatePasswordDto;
import licence.code.generator.entities.User;
import licence.code.generator.dto.RegisterUserDto;
import licence.code.generator.services.IUserService;
import licence.code.generator.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class RegistrationRestController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());


    private final IUserService userService;

    @Autowired
    public RegistrationRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = {"/register"})
    public ResponseEntity registerUser(@Valid final RegisterUserDto userDto, final ModelMap model) {
        LOGGER.debug("Registering user with information: {}", userDto);
        userService.registerUser(userDto);
        LOGGER.debug("User registered");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping(value = {"/user/updatePassword"})
    public ResponseEntity updateUserPassword(@Valid final UpdatePasswordDto passwordDto) {
        LOGGER.debug("Changing password with following dao: {}", passwordDto);
        final User user = userService.findUserByUsername(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        userService.changeUserPassword(user, passwordDto.getOldPassword(), passwordDto.getNewPassword());
        LOGGER.debug("Password changed for User with id: {}", user.getId());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }

    @PostMapping("/user/resetPassword")
    public String resetUserPassword() {
        User user = new User();
        user.setUsername("tak");
        //userService.registerUser(user);
        return "users";
    }
}
