package gov.usds.ecfr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AgencyTimeSeriesPointDto {

    private LocalDate snapshotDate;
    private long wordCount;
    private Double changeRatioVsPrev;
    private Double regulatoryDensityIndex;
    private boolean checksumChanged;
}