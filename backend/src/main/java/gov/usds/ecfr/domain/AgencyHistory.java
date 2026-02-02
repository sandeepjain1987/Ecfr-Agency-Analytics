package gov.usds.ecfr.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

//@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgencyHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Snapshot timestamp
    private LocalDateTime capturedAt;

    // Metrics at the time of ingestion
    private Long totalWordCount;

    private Integer complexityScore;

    private String checksum;

    // Which agency this snapshot belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id")
    private Agency agency;
}
