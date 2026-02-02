package gov.usds.ecfr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)

public class CfrReferenceDto {
    private Integer title;
    private String chapter;

//    @JsonProperty("subtitle")
    private String subtitle; // appears in some responses
    private String part;
}
