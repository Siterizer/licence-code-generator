package licence.code.generator.repositories;

import licence.code.generator.entities.Licence;
import licence.code.generator.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LicenceRepository extends JpaRepository<Licence, Long> {

    Licence findByUser(User user);

    @Override
    void delete(Licence role);

}
