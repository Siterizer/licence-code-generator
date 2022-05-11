package licence.code.generator.controllers;

import licence.code.generator.entities.User;
import licence.code.generator.dto.UserDto;
import licence.code.generator.services.IUserService;
import licence.code.generator.services.UserService;
import licence.code.generator.web.exception.UserAlreadyExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity registerUser(@Valid final UserDto userDto, final ModelMap model) {
        LOGGER.debug("Registering user with information: {}", userDto);
        try {
            userService.registerUser(userDto);


        } catch (UserAlreadyExistException e){
            String errMessage = "{\"message\":\"[{\"field\":\"email\",\"defaultMessage\":\"An account for that username/email already exists. Please enter a different username.\"}]}";

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(errMessage);
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping(value = {"/user/updatePassword"})
    public String updateUserPassword() {
        User user = new User();
        user.setUsername("tak");
        //userService.registerUser(user);
        return "users";
    }

    @PostMapping("/user/resetPassword")
    public String resetUserPassword() {
        User user = new User();
        user.setUsername("tak");
        //userService.registerUser(user);
        return "users";
    }
}
