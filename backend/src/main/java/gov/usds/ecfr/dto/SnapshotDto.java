package gov.usds.ecfr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class SnapshotDto {

    private Long id;
    private LocalDate snapshotDate;
    private String sourceVersionId;
}