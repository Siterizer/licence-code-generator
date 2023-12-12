package licence.code.generator.entities;


import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "product")
@Data
public class Product {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

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
