package gov.usds.ecfr.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgenciesResponse {
    private List<AgencyDto> agencies;

    public List<AgencyDto> getAgencies() {
        return agencies;
    }

}
