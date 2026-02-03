package gov.usds.ecfr.repository;

import gov.usds.ecfr.domain.CfrReferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CfrReferenceRepository extends JpaRepository<CfrReferenceEntity, Long> {
    @Query("""
    select c.title, c.chapter
    from CfrReferenceEntity c
    where c.agency.id = :agencyId
    """)
    List<Object[]> findTitleAndChapterByAgencyId(@Param("agencyId") Long agencyId);
}