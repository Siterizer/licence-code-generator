package licence.code.generator.integration.controller.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import licence.code.generator.dto.ProductDto;
import licence.code.generator.entities.Product;
import licence.code.generator.helper.JpaProductEntityHelper;
import licence.code.generator.helper.JpaUserEntityHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static licence.code.generator.util.GeneratorStringUtils.API_PATH;
import static licence.code.generator.util.GeneratorStringUtils.PRODUCT_GET_ALL_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProductRestControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private JpaProductEntityHelper jpaProductEntityHelper;
    @Autowired
    private JpaUserEntityHelper jpaUserEntityHelper;
    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void getAllProducts_shouldReturnMultipleProducts() throws Exception {
        //given:
        List<Product> expected = List.of(
                jpaProductEntityHelper.createRandomProduct(),
                jpaProductEntityHelper.createRandomProduct(),
                jpaProductEntityHelper.createRandomProduct());
        List<Long> expectedIds = expected.stream().map(Product::getId).toList();

        //when:
        MvcResult result = mvc.perform(get(API_PATH + PRODUCT_GET_ALL_PATH).with(user(jpaUserEntityHelper.createRandomUser()))
                .contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<ProductDto> resultDtoList = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        List<Long> resultIds = Objects.requireNonNull(resultDtoList).stream().map(ProductDto::id).toList();

        //then:
        assertTrue(resultIds.containsAll(expectedIds));
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void getAllProducts_shouldReturn401nOnNonLoggedInUse() throws Exception {
        //when-then:
        mvc.perform(get(API_PATH + PRODUCT_GET_ALL_PATH).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
