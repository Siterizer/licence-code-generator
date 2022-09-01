package licence.code.generator.entities;



import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name= "licence")
public class Licence {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GenericGenerator(name = "UUID", strategy = "uuid4")
    private UUID id;

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
