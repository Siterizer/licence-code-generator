package licence.code.generator.controllers.rest;

import licence.code.generator.dto.UserDto;
import licence.code.generator.dto.mapper.UserDtoMapper;
import licence.code.generator.entities.User;
import licence.code.generator.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@RestController
public class AdminRestController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    @Autowired
    public AdminRestController(UserService userService, UserDtoMapper userDtoMapper) {
        this.userService = userService;
        this.userDtoMapper = userDtoMapper;
    }

    @GetMapping(value = {"/admin/info"})
    public ResponseEntity<List<UserDto>> showAllUsers() {
        final User user = userService.findUserByUsername(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        LOGGER.debug("Showing all users for admin with id: {}", user.getId());
        List<UserDto> usersDto = userService.getAllUsers()
                .stream()
                .map(userDtoMapper::toDto)
                .sorted((Comparator.comparing(UserDto::getId)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(usersDto);
    }

    @PostMapping(value = {"/admin/block"})
    public ResponseEntity blockUser(@RequestParam(name = "id") Long id) {
        final User admin = userService.findUserByUsername(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        LOGGER.debug("Admin with id: {} attempts to block User with id: {}", admin.getId(), id);
        userService.blockUser(id, admin);
        LOGGER.debug("User blocked");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping(value = {"/admin/unblock"})
    public ResponseEntity unblockUser(@RequestParam(name = "id") Long id) {
        final User admin = userService.findUserByUsername(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        LOGGER.debug("Admin with id: {} attempts to unblock User with id: {}", admin.getId(), id);
        userService.unblockUser(id, admin);
        LOGGER.debug("User unblocked");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }
}
