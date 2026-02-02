package gov.usds.ecfr.service;

import gov.usds.ecfr.dto.AgencyMetricsResponse;
import gov.usds.ecfr.repository.PartRepository;
import org.springframework.stereotype.Service;

@Service
public class AgencyMetricsService {

//    private final RegulationTextRepository regulationTextRepository;
//    private final AgencyMetricsRepository agencyMetricsRepository;
    private final PartRepository partRepository;

/*    public MetricsService(RegulationTextRepository regulationTextRepository,
                          AgencyMetricsRepository agencyMetricsRepository) {
        this.regulationTextRepository = regulationTextRepository;
        this.agencyMetricsRepository = agencyMetricsRepository;
    }*/

    /*public AgencyMetrics computeMetricsForAgencySnapshot(Agency agency, Snapshot snapshot) {
        List<RegulationText> texts = regulationTextRepository.findByAgencyAndSnapshot(agency, snapshot);

        long totalWords = texts.stream()
                .mapToLong(rt -> TextUtils.countWords(rt.getRawText()))
                .sum();

        String concatenated = texts.stream()
                .sorted(Comparator.comparing(RegulationText::getPath))
                .map(RegulationText::getRawText)
                .reduce("", (a, b) -> a + "\n" + b);

        String checksum = HashUtils.sha256(concatenated);

        double rdi = texts.isEmpty() ? 0.0 : (double) totalWords / texts.size();

        AgencyMetrics metrics = new AgencyMetrics();
        metrics.setAgency(agency);
        metrics.setSnapshot(snapshot);
        metrics.setWordCount(totalWords);
        metrics.setChecksum(checksum);
        metrics.setRegulatoryDensityIndex(rdi);
        metrics.setCreatedAt(LocalDateTime.now());

        // changeRatioVsPrev will be filled by caller after looking up previous snapshot
        return metrics;
    }*/



    public AgencyMetricsService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public AgencyMetricsResponse getMetricsForAgency(Long agencyId) {

        long totalParts = partRepository.countPartsByAgency(agencyId);
        Long totalWords = partRepository.getTotalWordCountByAgency(agencyId);
        Double totalComplexity = partRepository.totalComplexityByAgency(agencyId);

        return new AgencyMetricsResponse(
                agencyId,
                totalParts,
                totalWords != null ? totalWords : 0,
                totalComplexity != null ? totalComplexity : 0.0
        );
    }


}