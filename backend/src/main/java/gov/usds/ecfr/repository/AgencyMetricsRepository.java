package gov.usds.ecfr.repository;

import gov.usds.ecfr.domain.Agency;
import gov.usds.ecfr.model.AgencyMetrics;
import gov.usds.ecfr.model.Snapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgencyMetricsRepository extends JpaRepository<AgencyMetrics, Long> {

    List<AgencyMetrics> findBySnapshot(Snapshot snapshot);

    List<AgencyMetrics> findByAgencyOrderBySnapshot_SnapshotDateAsc(Agency agency);

    Optional<AgencyMetrics> findTopByAgencyOrderBySnapshot_SnapshotDateDesc(Agency agency);

    Optional<AgencyMetrics> findTopByAgencyAndSnapshot_SnapshotDateLessThanOrderBySnapshot_SnapshotDateDesc(
            Agency agency, java.time.LocalDate snapshotDate);
}