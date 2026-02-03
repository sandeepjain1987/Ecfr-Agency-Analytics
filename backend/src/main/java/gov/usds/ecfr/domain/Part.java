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

    private Integer titleNumber;
    private String partNumber;

    @Column(columnDefinition = "TEXT")
    private String text;

    private Long wordCount;
    private Integer complexityScore;
    private String checksum;

    // Many parts belong to one agency
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id")
    private Agency agency;
}
