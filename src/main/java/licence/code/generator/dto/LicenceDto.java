package licence.code.generator.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LicenceDto {
    private final String name;
    private final String licence;


    @Override
    public String toString() {
        return "LicenceDto [name=" +
                name +
                ", licence=" +
                licence +
                "]";
    }
}
