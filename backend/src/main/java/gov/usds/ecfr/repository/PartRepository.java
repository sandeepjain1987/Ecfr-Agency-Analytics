package gov.usds.ecfr.repository;

import gov.usds.ecfr.domain.Part;
import gov.usds.ecfr.dto.AgencySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PartRepository extends JpaRepository<Part, Long> {

    /*@Query("SELECT COUNT(p) FROM Part p")
    long countParts();

    @Query("SELECT SUM(p.wordCount) FROM Part p")
    Long sumWordCount();

    @Query("SELECT AVG(p.complexityScore) FROM Part p")
    Double avgComplexity();*/

/*    @Query("SELECT COUNT(p) FROM Part p WHERE p.agency.id = :agencyId")
    long countPartsByAgency(Long agencyId);*/

    @Query("SELECT COUNT(DISTINCT p.partNumber) FROM Part p WHERE p.agency.id = :agencyId")
    long countPartsByAgency(Long agencyId);

    /*@Query("SELECT SUM(p.wordCount) FROM Part p WHERE p.agency.id = :agencyId")
    Long sumWordCountByAgency(Long agencyId);*/

    @Query("SELECT a.totalWordCount FROM Agency a WHERE a.id = :agencyId")
    Long getTotalWordCountByAgency(Long agencyId);

    @Query("SELECT a.complexityScore FROM Agency a WHERE a.id = :agencyId")
    Double totalComplexityByAgency(Long agencyId);

//    @Query("SELECT new gov.usds.ecfr.dto.AgencySummary(a.id, a.name, COALESCE(COUNT(p.wordCount), 0)) " +
//            "FROM Agency a LEFT JOIN Part p ON p.agency.id = a.id " +
//            "GROUP BY a.id, a.name")
//    List<AgencySummary> getAllAgencyWordCounts();

    @Query("SELECT new gov.usds.ecfr.dto.AgencySummary(a.id, a.name, COALESCE(a.totalWordCount, 0)) " +
            "FROM Agency a")
    List<AgencySummary> getAllAgencyWordCounts();

}

