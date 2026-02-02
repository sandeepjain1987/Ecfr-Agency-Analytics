package gov.usds.ecfr.model;

import gov.usds.ecfr.domain.Agency;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "agency_metrics")
@Getter
@Setter
public class AgencyMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Agency agency;

    @ManyToOne(fetch = FetchType.LAZY)
    private Snapshot snapshot;

    private long wordCount;

    private String checksum;

    private Double changeRatioVsPrev;

    private Double regulatoryDensityIndex; // custom metric

    private LocalDateTime createdAt;
}