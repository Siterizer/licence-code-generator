package licence.code.generator.dto;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserEmailDto {
    private final Long id;
    private final String email;


    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "UserEmailDto [id=" +
                id +
                ", email=" +
                email + "]";
    }
}
