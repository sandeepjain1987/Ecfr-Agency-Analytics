package gov.usds.ecfr.service;

import gov.usds.ecfr.dto.AgencySummary;
import gov.usds.ecfr.repository.PartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgencySummaryService {

    private final PartRepository partRepository;

    public AgencySummaryService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public List<AgencySummary> getAllAgencySummaries() {
        return partRepository.getAllAgencyWordCounts();
    }
}
