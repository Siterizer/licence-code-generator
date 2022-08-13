package licence.code.generator.controllers;

import licence.code.generator.dto.UserEmailDto;
import licence.code.generator.dto.mapper.UserDtoMapper;
import licence.code.generator.dto.mapper.UserEmailDtoMapper;
import licence.code.generator.entities.User;
import licence.code.generator.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static java.util.stream.Collectors.toList;


@RestController
public class UserRestController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;
    private final UserEmailDtoMapper userEmailDtoMapper;

    @Autowired
    public UserRestController(UserService userService, UserDtoMapper userDtoMapper, UserEmailDtoMapper userEmailDtoMapper) {
        this.userService = userService;
        this.userDtoMapper = userDtoMapper;
        this.userEmailDtoMapper = userEmailDtoMapper;
    }

    @RequestMapping(value="/user/info", method=RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserEmailDto> getCurrentUserEmail(Model model) {
        final User user = userService.findUserByUsername(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        LOGGER.debug("Showing email for user with id: {}", user.getId());
        UserEmailDto dto = userEmailDtoMapper.toDto(user);


        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = {"/admin/info"})
    public String showAllUsers(Model model) {
        final User user = userService.findUserByUsername(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        LOGGER.debug("Showing all users for admin with id: {}", user.getId());
        model.addAttribute("users",
                userService.getAllUsers()
                        .stream()
                        .map(userDtoMapper::toDto)
                        .collect(toList()));
        return "admin";
    }
}

