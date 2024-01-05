package licence.code.generator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private BigDecimal price;

    @Override
    public String toString() {
        return "ProductDto [id=" +
                id +
                ", name=" +
                name +
                ", price=" +
                price +
                "]";
    }
}
