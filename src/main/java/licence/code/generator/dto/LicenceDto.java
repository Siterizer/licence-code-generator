package licence.code.generator.dto;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class LicenceDto {
    private final String name;
    private final String licence;


    public String getName() {
        return name;
    }

    public String getLicence() {
        return licence;
    }

    @Override
    public String toString() {
        return "LicenceDto [name=" +
                name +
                ", licence=" +
                licence +
                "]";
    }
}
