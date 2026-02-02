package gov.usds.ecfr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AgencySummaryDto {

    private Long id;
    private String name;
    private long wordCount;
    private Double changeRatioVsPrev;
    private Double regulatoryDensityIndex;
    private boolean checksumChanged;
}