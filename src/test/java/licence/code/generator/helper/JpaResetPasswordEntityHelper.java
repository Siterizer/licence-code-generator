package licence.code.generator.helper;

import licence.code.generator.entities.Licence;
import licence.code.generator.entities.User;
import licence.code.generator.repositories.LicenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JpaLicenceEntityHelper {

    @Autowired
    LicenceRepository licenceRepository;
    @Autowired
    JpaProductEntityHelper productEntityHelper;
    @Autowired
    JpaUserEntityHelper userEntityHelper;

    public Licence createRandomLicence() {
        Licence licence = new Licence();
        licence.setProduct(productEntityHelper.createRandomProduct());
        licence.setUser(userEntityHelper.createRandomUser());
        licenceRepository.save(licence);
        return licence;
    }

    public Licence addRandomLicenceToExistingUser(User user) {
        Licence licence = new Licence();
        licence.setProduct(productEntityHelper.createRandomProduct());
        licence.setUser(user);
        licenceRepository.save(licence);
        return licence;
    }
}
