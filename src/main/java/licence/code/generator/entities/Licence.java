package licence.code.generator.entities;



import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name= "licence")
public class Licence {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "uuid", unique = true)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    public Licence(User user, Product product) {
        this.user = user;
        this.product = product;
    }

    public Licence() {}

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }


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
