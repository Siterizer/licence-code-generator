package licence.code.generator.controllers.rest;

import licence.code.generator.dto.RegisterUserDto;
import licence.code.generator.services.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class RegistrationRestController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final IUserService userService;

    @Autowired
    public RegistrationRestController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = {"/register"})
    public ResponseEntity<?> registerUser(@Valid final RegisterUserDto userDto) {
        LOGGER.info("Registering user with information: {}", userDto);
        userService.registerUser(userDto);
        LOGGER.info("User: {} registered", userDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
