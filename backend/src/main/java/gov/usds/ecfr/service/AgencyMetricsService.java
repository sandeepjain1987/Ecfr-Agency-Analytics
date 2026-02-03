package gov.usds.ecfr.service;

import gov.usds.ecfr.dto.AgencyMetricsResponse;
import gov.usds.ecfr.repository.PartRepository;
import org.springframework.stereotype.Service;

@Service
public class AgencyMetricsService {

    private final PartRepository partRepository;

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