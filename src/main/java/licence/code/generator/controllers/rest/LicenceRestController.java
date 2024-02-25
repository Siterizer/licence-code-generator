package licence.code.generator.controllers.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import licence.code.generator.dto.IdRequestDto;
import licence.code.generator.entities.User;
import licence.code.generator.services.ILicenceService;
import licence.code.generator.services.user.IUserService;
import licence.code.generator.web.exception.UnauthorizedUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static licence.code.generator.util.GeneratorStringUtils.API_PATH;
import static licence.code.generator.util.GeneratorStringUtils.LICENCE_BUY_PATH;

@Tag(name = "Licence", description = "Licence Rest API")
@RestController
@RequestMapping(API_PATH)
public class LicenceRestController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final IUserService userService;
    private final ILicenceService licenceService;

    @Autowired
    public LicenceRestController(IUserService userService, ILicenceService licenceService) {
        this.userService = userService;
        this.licenceService = licenceService;
    }

    @Operation(
            summary = "Buy Licence for current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Null parameter"),
            @ApiResponse(responseCode = "401", description = "Requester is not logged-in"),
            @ApiResponse(responseCode = "404", description = "licence id does not exists")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping(value = {LICENCE_BUY_PATH})
    public ResponseEntity<?> buyLicence(@RequestBody IdRequestDto idRequestDto) {
        User requester = getRequester();
        LOGGER.info("User with id: {} attempts to buy Licence for productId: {}", requester.getId(), idRequestDto.id());
        licenceService.createLicence(requester.getId(), idRequestDto.id());
        LOGGER.info("Created Licence for User with id: {} with productId: {}", requester.getId(), idRequestDto.id());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private User getRequester() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            //Note this is never going to happen in real-life usage case as Spring Security prevents any Unauthorized
            //entrance with RestAuthenticationEntryPoint class
            throw new UnauthorizedUserException("Unauthorized User wanted to access /admin endpoint");
        }
        return (User) authentication.getPrincipal();
    }
}
