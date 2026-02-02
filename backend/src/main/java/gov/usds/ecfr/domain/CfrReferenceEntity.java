package gov.usds.ecfr.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CfrReferenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer title;
    private String chapter;

//    @ManyToOne
   // @JoinColumn(name = "part_id")
//    private Part part;
private String part;
    private String subtitle;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "agency_id")
    private Agency agency;


}