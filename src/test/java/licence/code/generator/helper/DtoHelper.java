package licence.code.generator.helper;

import licence.code.generator.dto.RegisterUserDto;
import licence.code.generator.dto.UpdatePasswordDto;
import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Component;

@Component
public class DtoHelper {
    public RegisterUserDto createRandomRegisterUserDto() {
        String password = RandomString.make() + "1";
        return new RegisterUserDto(RandomString.make(), RandomString.make() + "@email.com", password, password);
    }

    public UpdatePasswordDto createRandomUpdatePasswordDto(String oldPassword) {
        String newPassword = RandomString.make();
        return new UpdatePasswordDto(oldPassword, newPassword, newPassword);
    }
}
