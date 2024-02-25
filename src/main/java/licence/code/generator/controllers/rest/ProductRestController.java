package licence.code.generator.controllers.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import licence.code.generator.dto.ProductDto;
import licence.code.generator.entities.User;
import licence.code.generator.services.IProductService;
import licence.code.generator.services.user.IUserService;
import licence.code.generator.web.exception.UnauthorizedUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

import static licence.code.generator.util.GeneratorStringUtils.API_PATH;
import static licence.code.generator.util.GeneratorStringUtils.PRODUCT_GET_ALL_PATH;

@Tag(name = "Product", description = "Product Rest API")
@RestController
@RequestMapping(API_PATH)
public class ProductRestController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final IProductService productService;
    private final IUserService userService;

    @Autowired
    ProductRestController(IProductService productService, IUserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @Operation(
            summary = "Buy Licence for current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Requester is not logged-in"),
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = {PRODUCT_GET_ALL_PATH})
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        User requester = getRequester();
        LOGGER.info("Showing all products for user with id: {}", requester.getId());
        List<ProductDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    private User getRequester() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            //Note this is never going to happen in real-life usage case as Spring Security prevents any Unauthorized
            //entrance with RestAuthenticationEntryPoint class
            throw new UnauthorizedUserException("Unauthorized User wanted to access /product endpoint");
        }
        return (User) authentication.getPrincipal();
    }

}
