package licence.code.generator.integration.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import licence.code.generator.dto.IdRequestDto;
import licence.code.generator.entities.Licence;
import licence.code.generator.entities.Product;
import licence.code.generator.entities.User;
import licence.code.generator.helper.JpaProductEntityHelper;
import licence.code.generator.helper.JpaUserEntityHelper;
import licence.code.generator.repositories.LicenceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static licence.code.generator.util.GeneratorStringUtils.API_PATH;
import static licence.code.generator.util.GeneratorStringUtils.LICENCE_BUY_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class LicenceRestControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private JpaProductEntityHelper jpaProductEntityHelper;
    @Autowired
    private JpaUserEntityHelper jpaUserEntityHelper;
    @Autowired
    private LicenceRepository licenceRepository;
    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void buyLicence_shouldCreateNewLicence() throws Exception {
        //given:
        User user = jpaUserEntityHelper.createRandomUser();
        Product product = jpaProductEntityHelper.createRandomProduct();

        //when:
        MvcResult result = mvc.perform(post(API_PATH + LICENCE_BUY_PATH).with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new IdRequestDto(product.getId()))))
                .andReturn();

        //then:
        assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
        Licence createdLicence = licenceRepository.findByUser(user).get(0);
        assertEquals(product.getId(), createdLicence.getProduct().getId());
        assertEquals(user.getId(), createdLicence.getUser().getId());
    }

    @Test
    public void buyLicence_shouldCreateMultipleLicencesForTheSameProduct() throws Exception {
        //given:
        User user = jpaUserEntityHelper.createRandomUser();
        Product product = jpaProductEntityHelper.createRandomProduct();

        //when:
        mvc.perform(post(API_PATH + LICENCE_BUY_PATH).with(user(user)).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(new IdRequestDto(product.getId()))));
        mvc.perform(post(API_PATH + LICENCE_BUY_PATH).with(user(user)).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(new IdRequestDto(product.getId()))));
        mvc.perform(post(API_PATH + LICENCE_BUY_PATH).with(user(user)).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(new IdRequestDto(product.getId()))));

        //then:
        List<Licence> createdLicences = licenceRepository.findByUser(user);
        assertEquals(3, createdLicences.size());
        createdLicences.forEach(e -> assertEquals(product.getId(), e.getProduct().getId()));
    }

    @Test
    public void buyLicence_shouldCreateMultipleLicencesForMultipleUsers() throws Exception {
        //given:
        User user1 = jpaUserEntityHelper.createRandomUser();
        User user2 = jpaUserEntityHelper.createRandomUser();
        User user3 = jpaUserEntityHelper.createRandomUser();
        Product product1 = jpaProductEntityHelper.createRandomProduct();
        Product product2 = jpaProductEntityHelper.createRandomProduct();
        Product product3 = jpaProductEntityHelper.createRandomProduct();

        //when:
        mvc.perform(post(API_PATH + LICENCE_BUY_PATH).with(user(user1)).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(new IdRequestDto(product1.getId()))));
        mvc.perform(post(API_PATH + LICENCE_BUY_PATH).with(user(user1)).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(new IdRequestDto(product2.getId()))));
        mvc.perform(post(API_PATH + LICENCE_BUY_PATH).with(user(user1)).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(new IdRequestDto(product3.getId()))));
        mvc.perform(post(API_PATH + LICENCE_BUY_PATH).with(user(user2)).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(new IdRequestDto(product1.getId()))));
        mvc.perform(post(API_PATH + LICENCE_BUY_PATH).with(user(user3)).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(new IdRequestDto(product2.getId()))));
        mvc.perform(post(API_PATH + LICENCE_BUY_PATH).with(user(user3)).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(new IdRequestDto(product3.getId()))));

        //then:
        assertEquals(3, licenceRepository.findByUser(user1).size());
        assertEquals(1, licenceRepository.findByUser(user2).size());
        assertEquals(2, licenceRepository.findByUser(user3).size());
    }

    @Test
    public void buyLicence_shouldReturn404CodeForNonExistingProduct() throws Exception {
        //given:
        User user = jpaUserEntityHelper.createRandomUser();

        //when-then:
        mvc.perform(post(API_PATH + LICENCE_BUY_PATH).with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        //some "random" Id
                        .content(mapper.writeValueAsString(new IdRequestDto(3465713455L))))
                .andExpect(status().isNotFound());
    }

    @Test
    public void buyLicence_shouldReturn400CodeForNullProduct() throws Exception {
        //given:
        User user = jpaUserEntityHelper.createRandomUser();

        //when-then:
        mvc.perform(post(API_PATH + LICENCE_BUY_PATH).with(user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buyLicence_shouldReturn401nOnNonLoggedInUse() throws Exception {
        //when-then:
        mvc.perform(post(API_PATH + LICENCE_BUY_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new IdRequestDto(3465713455L))))
                .andExpect(status().isUnauthorized());
    }
}
