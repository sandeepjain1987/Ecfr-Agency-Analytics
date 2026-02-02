package gov.usds.ecfr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AgencyDetailDto {

    private Long id;
    private String name;
    private List<AgencyTimeSeriesPointDto> history;
}