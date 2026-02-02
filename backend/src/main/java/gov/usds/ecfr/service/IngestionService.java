package gov.usds.ecfr.service;

import gov.usds.ecfr.repository.AgencyMetricsRepository;
import gov.usds.ecfr.repository.AgencyRepository;
import gov.usds.ecfr.repository.SnapshotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IngestionService {

    private final EcfrClient ecfrClient;
    private final AgencyRepository agencyRepository;
    private final SnapshotRepository snapshotRepository;
    private final AgencyMetricsRepository agencyMetricsRepository;
    private final AgencyMetricsService agencyMetricsService;

    public IngestionService(EcfrClient ecfrClient,
                            AgencyRepository agencyRepository,
                            SnapshotRepository snapshotRepository,
                            AgencyMetricsRepository agencyMetricsRepository,
                            AgencyMetricsService agencyMetricsService) {
        this.ecfrClient = ecfrClient;
        this.agencyRepository = agencyRepository;
        this.snapshotRepository = snapshotRepository;
        this.agencyMetricsRepository = agencyMetricsRepository;
        this.agencyMetricsService = agencyMetricsService;
    }

    @Transactional
    public void ingestCurrentEcfr() {
        // 1. Fetch raw data from eCFR
       /* String raw = ecfrClient.fetchRawData();
        // TODO: parse raw JSON, map to agencies + regulation texts, persist them

        // 2. Create snapshot
        Snapshot snapshot = new Snapshot();
        snapshot.setSnapshotDate(LocalDate.now());
        snapshot.setSourceVersionId("TODO"); // from API if available
        snapshot.setCreatedAt(LocalDateTime.now());
        snapshot = snapshotRepository.save(snapshot);

        // 3. For each agency, compute metrics
        List<Agency> agencies = agencyRepository.findAll();
        for (Agency agency : agencies) {
            AgencyMetrics metrics = metricsService.computeMetricsForAgencySnapshot(agency, snapshot);

            // 4. Compute change vs previous snapshot
            agencyMetricsRepository
                    .findTopByAgencyOrderBySnapshot_SnapshotDateDesc(agency)
                    .ifPresent(prev -> {
                        double prevWords = prev.getWordCount();
                        double ratio = prevWords == 0 ? 0.0 :
                                (metrics.getWordCount() - prevWords) / prevWords;
                        metrics.setChangeRatioVsPrev(ratio);
                    });

            agencyMetricsRepository.save(metrics);*/
        }
    }
