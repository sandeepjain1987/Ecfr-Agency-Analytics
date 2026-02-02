package gov.usds.ecfr.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gov.usds.ecfr.dto.AgencyDto;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgenciesResponse {
    private List<AgencyDto> agencies;

    public List<AgencyDto> getAgencies() {
        return agencies;
    }

}
