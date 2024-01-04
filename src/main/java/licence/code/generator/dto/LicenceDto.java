package licence.code.generator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LicenceDto {
    private String name;
    private String licence;

    @Override
    public String toString() {
        return "LicenceDto [name=" +
                name +
                ", licence=" +
                licence +
                "]";
    }
}
