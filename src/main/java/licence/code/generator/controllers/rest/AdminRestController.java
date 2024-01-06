package licence.code.generator.controllers.rest;

import licence.code.generator.dto.IdRequestDto;
import licence.code.generator.dto.UserDto;
import licence.code.generator.entities.User;
import licence.code.generator.services.IUserService;
import licence.code.generator.web.exception.UnauthorizedUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

import static licence.code.generator.util.GeneratorStringUtils.*;


@RestController
public class AdminRestController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final IUserService userService;

    @Autowired
    public AdminRestController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = {ADMIN_INFO_PATH})
    public ResponseEntity<List<UserDto>> showAllUsers() {
        User requester = getRequester();
        LOGGER.info("Showing all users for admin with id: {}", requester.getId());
        List<UserDto> usersDto = userService.getAllUsersDto(requester);
        return ResponseEntity.ok(usersDto);
    }

    @PostMapping(value = {ADMIN_BLOCK_PATH})
    public ResponseEntity<?> blockUser(@RequestBody IdRequestDto idRequestDto) {
        User requester = getRequester();
        LOGGER.info("Admin with id: {} attempts to block User with id: {}", requester.getId(), idRequestDto);
        userService.blockUser(idRequestDto.id(), requester);
        LOGGER.info("User with id: {} blocked by Admin with id: {}", idRequestDto.id(), requester.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping(value = {ADMIN_UNBLOCK_PATH})
    public ResponseEntity<?> unblockUser(@RequestBody IdRequestDto idRequestDto) {
        User requester = getRequester();
        LOGGER.info("Admin with id: {} attempts to unblock User with id: {}", requester.getId(), idRequestDto);
        userService.unblockUser(idRequestDto.id(), requester);
        LOGGER.info("User with id: {} unblocked by Admin with id: {}", idRequestDto.id(), requester.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private User getRequester() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            //Note this is never going to happen in real-life usage case as Spring Security prevents any Unauthorized
            //entrance with RestAuthenticationEntryPoint class
            throw new UnauthorizedUserException("Unauthorized User wanted to access /admin endpoint");
        }
        return userService.findUserByUsername(authentication.getName());
    }
}

