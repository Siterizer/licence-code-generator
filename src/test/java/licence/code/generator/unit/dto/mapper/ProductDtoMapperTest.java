package licence.code.generator.unit.dto.mapper;

import licence.code.generator.dto.ProductDto;
import licence.code.generator.dto.mapper.ProductDtoMapper;
import licence.code.generator.entities.Product;
import licence.code.generator.helper.JpaProductEntityHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
class ProductDtoMapperTest {

    @Autowired
    JpaProductEntityHelper jpaProductEntityHelper;
    @Autowired
    ProductDtoMapper dtoMapper;

    @Test
    public void toDto_shouldPerformMapping() {
        //given:
        Product product = jpaProductEntityHelper.createRandomProduct();

        //when:
        ProductDto result = dtoMapper.toDto(product);

        //then:
        assertEquals(result.getName(), product.getName());
        assertEquals(result.getId(), product.getId());
    }
}