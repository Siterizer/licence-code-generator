package licence.code.generator.entities;



import javax.persistence.*;

@Entity
@Table(name= "product")
public class Product {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private final String name;

    public Product(String name) {
        this.name = name;
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {

        return "Product [id=" +
                id +
                ", name=" +
                name +
                "]";
    }
}
