package licence.code.generator.entities;


import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.Collection;

@Entity
@Table(name = "product")
@Data
public class Product {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Digits(integer = 5, fraction = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "product")
    Collection<Licence> licences;

    @Override
    public String toString() {
        return "Product [id=" +
                id +
                ", name=" +
                name +
                "]";
    }
}
