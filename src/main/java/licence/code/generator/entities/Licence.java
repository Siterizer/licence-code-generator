package licence.code.generator.entities;


import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "licence")
@Data
public class Licence {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "uuid", unique = true)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    @Override
    public String toString() {
        return "Licence [id=" +
                id +
                ", user=" +
                user.getId() +
                ", product=" +
                product.getId() +
                "]";
    }
}
