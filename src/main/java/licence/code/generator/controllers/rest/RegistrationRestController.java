package licence.code.generator.controllers.rest;

import jakarta.validation.Valid;
import licence.code.generator.dto.RegisterUserDto;
import licence.code.generator.services.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static licence.code.generator.util.GeneratorStringUtils.REGISTER_PATH;

@Controller
public class RegistrationRestController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final IUserService userService;

    @Autowired
    public RegistrationRestController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = {REGISTER_PATH})
    public ResponseEntity<?> registerUser(@Valid @RequestBody final RegisterUserDto userDto) {
        System.out.println("halo123");
        LOGGER.info("Registering user with information: {}", userDto);
        userService.registerUser(userDto);
        LOGGER.info("User: {} registered", userDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
