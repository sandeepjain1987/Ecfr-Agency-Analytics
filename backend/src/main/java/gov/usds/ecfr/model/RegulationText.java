package gov.usds.ecfr.model;

import gov.usds.ecfr.domain.Agency;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "regulation_text")
@Getter
@Setter
public class RegulationText {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Agency agency;

    @ManyToOne(fetch = FetchType.LAZY)
    private Snapshot snapshot;

    private String path;

    @Lob
    private String rawText;
}