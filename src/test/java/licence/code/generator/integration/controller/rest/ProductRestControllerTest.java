package licence.code.generator.integration.controller.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import licence.code.generator.dto.IdRequestDto;
import licence.code.generator.dto.ProductDto;
import licence.code.generator.entities.Product;
import licence.code.generator.helper.JpaProductEntityHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static licence.code.generator.util.GeneratorStringUtils.LICENCE_BUY_PATH;
import static licence.code.generator.util.GeneratorStringUtils.PRODUCT_GET_ALL_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProductRestControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private JpaProductEntityHelper jpaProductEntityHelper;
    ObjectMapper mapper = new ObjectMapper();

    @Test
    @WithMockUser(username = "user1")
    public void getAllProducts_shouldReturnMultipleProducts() throws Exception {
        //given:
        List<Product> expected = List.of(
                jpaProductEntityHelper.createRandomProduct(),
                jpaProductEntityHelper.createRandomProduct(),
                jpaProductEntityHelper.createRandomProduct());
        List<Long> expectedIds = expected.stream().map(Product::getId).collect(Collectors.toList());

        //when:
        MvcResult result = mvc.perform(get(PRODUCT_GET_ALL_PATH).contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<ProductDto> resultDtoList = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        List<Long> resultIds = Objects.requireNonNull(resultDtoList).stream().map(ProductDto::getId).collect(Collectors.toList());

        //then:
        assertTrue(resultIds.containsAll(expectedIds));
        assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
    }

    @Test
    void getAllProducts_shouldReturn401nOnNonLoggedInUse() throws Exception {
        //when-then:
        mvc.perform(get(PRODUCT_GET_ALL_PATH).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
