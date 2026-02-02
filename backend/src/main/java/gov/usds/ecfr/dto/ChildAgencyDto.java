package gov.usds.ecfr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)

public class ChildAgencyDto {

    private String name;

    @JsonProperty("short_name")
    private String shortName;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("sortable_name")
    private String sortableName;

    private String slug;

    @JsonProperty("cfr_references")
    private List<CfrReferenceDTO> cfrReferences;
}

