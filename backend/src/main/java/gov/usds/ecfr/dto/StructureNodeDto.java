package gov.usds.ecfr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class StructureNodeDto {
    private String identifier;

    private String label;

    @JsonProperty("label_level")
    private String labelLevel;   // <-- MUST be String

    @JsonProperty("label_description")
    private String labelDescription;

    private boolean reserved;

    private String type;

    private Integer size;

    private List<String> volumes;

    private List<StructureNodeDto> children;

    @JsonProperty("descendant_range")
    private String descendantRange;

    @JsonProperty("received_on")
    private String receivedOn;


}