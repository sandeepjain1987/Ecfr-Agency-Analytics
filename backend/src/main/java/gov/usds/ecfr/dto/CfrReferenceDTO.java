package gov.usds.ecfr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.usds.ecfr.domain.Part;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)

public class CfrReferenceDTO {
    private Integer title;
    private String chapter;

//    @JsonProperty("subtitle")
    private String subtitle; // appears in some responses
    private String part;
}
