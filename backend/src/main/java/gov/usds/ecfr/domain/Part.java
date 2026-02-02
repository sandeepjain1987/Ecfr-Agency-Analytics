package gov.usds.ecfr.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // eCFR Title number (e.g., 1, 5, 12, 40)
    private Integer titleNumber;

    // eCFR Part number (e.g., "301", "9301")
    private String partNumber;

    // Full extracted text from XML
    @Lob
    @Column(columnDefinition = "CLOB")
    private String text;

    // Computed metrics
    private Long wordCount;

    private Integer complexityScore;

    private String checksum;

    // Many parts belong to one agency
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id")
    private Agency agency;
}
