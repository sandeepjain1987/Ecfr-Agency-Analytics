package gov.usds.ecfr.repository;

import gov.usds.ecfr.model.Snapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SnapshotRepository extends JpaRepository<Snapshot, Long> {

    Optional<Snapshot> findTopByOrderBySnapshotDateDesc();

    Optional<Snapshot> findBySnapshotDate(LocalDate snapshotDate);
}