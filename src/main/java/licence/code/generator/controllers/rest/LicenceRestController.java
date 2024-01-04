package licence.code.generator.controllers.rest;

import licence.code.generator.dto.IdRequestDto;
import licence.code.generator.entities.User;
import licence.code.generator.services.ILicenceService;
import licence.code.generator.services.IUserService;
import licence.code.generator.web.exception.UnauthorizedUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static licence.code.generator.util.GeneratorStringUtils.LICENCE_BUY_PATH;

@RestController
public class LicenceRestController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final IUserService userService;
    private final ILicenceService licenceService;

    @Autowired
    public LicenceRestController(IUserService userService, ILicenceService licenceService) {
        this.userService = userService;
        this.licenceService = licenceService;
    }

    @PostMapping(value = {LICENCE_BUY_PATH})
    public ResponseEntity<?> buyLicence(@RequestBody IdRequestDto idRequestDto) {
        User requester = getRequester();
        LOGGER.info("User with id: {} attempts to buy Licence for productId: {}", requester.getId(), idRequestDto.getId());
        licenceService.createLicence(requester.getId(), idRequestDto.getId());
        LOGGER.info("Created Licence for User with id: {} with productId: {}", requester.getId(), idRequestDto.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
