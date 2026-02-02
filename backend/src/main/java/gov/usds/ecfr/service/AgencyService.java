package gov.usds.ecfr.service;

import gov.usds.ecfr.domain.Agency;
import gov.usds.ecfr.domain.CfrReferenceEntity;
import gov.usds.ecfr.repository.AgencyRepository;
import org.springframework.stereotype.Service;
import gov.usds.ecfr.dto.AgenciesResponse;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AgencyService {


    private final AgencyRepository agencyRepository;
//    private final AgencyMetricsRepository agencyMetricsRepository;
//    private final SnapshotRepository snapshotRepository;
    private final RestTemplate rest = new RestTemplate();

    public AgencyService(AgencyRepository agencyRepository) {
        this.agencyRepository = agencyRepository;
    }

    public void fetchAndStoreAgencies() {
        String url = "https://www.ecfr.gov/api/admin/v1/agencies.json";

            AgenciesResponse response = rest.getForObject(url, AgenciesResponse.class);
            //System.out.println(response);

        if (response == null || response.getAgencies() == null) return;

        response.getAgencies().forEach(dto -> {
            Agency parent = new Agency();

            parent.setName(dto.getName());
            parent.setShortName(dto.getShortName());
            parent.setDisplayName(dto.getDisplayName());
            parent.setSortableName(dto.getSortableName());
            parent.setSlug(dto.getSlug());

            List<CfrReferenceEntity> cfrrefs = dto.getCfrReferences().stream().map(refDto -> {
                String section = "";
                CfrReferenceEntity ref = new CfrReferenceEntity();
                ref.setTitle(refDto.getTitle());
                ref.setChapter(refDto.getChapter());
                ref.setPart(refDto.getPart());
                ref.setSubtitle(refDto.getSubtitle());
                ref.setAgency(parent);
                return ref;
            }).toList();

            List<Agency> childrenRefs = dto.getChildren().stream().map(refDto -> {
                Agency ref = new Agency();
                ref.setName(refDto.getName());
                ref.setShortName(dto.getShortName());
                ref.setDisplayName(dto.getDisplayName());
                ref.setSortableName(dto.getSortableName());
                ref.setSlug(dto.getSlug());
               // ref.setParent(parent);
               // parent.getChildren().add(ref);

                return ref;
            }).toList();

            parent.setCfrReferences(cfrrefs);
            parent.setChildren(childrenRefs);
            agencyRepository.save(parent);
        });
    }


/*    public AgencyService(AgencyRepository agencyRepository,
                         AgencyMetricsRepository agencyMetricsRepository,
                         SnapshotRepository snapshotRepository) {
        this.agencyRepository = agencyRepository;
        this.agencyMetricsRepository = agencyMetricsRepository;
        this.snapshotRepository = snapshotRepository;
    }*/

    /*public List<AgencySummaryDto> getAgenciesForSnapshot(LocalDate snapshotDate) {
        Snapshot snapshot = snapshotDate != null
                ? snapshotRepository.findBySnapshotDate(snapshotDate).orElseThrow()
                : snapshotRepository.findTopByOrderBySnapshotDateDesc().orElseThrow();

        List<AgencyMetrics> metricsList = agencyMetricsRepository.findBySnapshot(snapshot);

        return metricsList.stream()
                .map(m -> {
                    boolean checksumChanged = m.getChangeRatioVsPrev() != null
                            && Math.abs(m.getChangeRatioVsPrev()) > 0.0;
                    return new AgencySummaryDto(
                            m.getAgency().getId(),
                            m.getAgency().getName(),
                            m.getWordCount(),
                            m.getChangeRatioVsPrev(),
                            m.getRegulatoryDensityIndex(),
                            checksumChanged
                    );
                })
                .collect(Collectors.toList());
    }

    public AgencyDetailDto getAgencyDetail(Long agencyId) {
        Agency agency = agencyRepository.findById(agencyId).orElseThrow();
        List<AgencyMetrics> history = agencyMetricsRepository
                .findByAgencyOrderBySnapshot_SnapshotDateAsc(agency);

        List<AgencyTimeSeriesPointDto> points = history.stream()
                .map(m -> new AgencyTimeSeriesPointDto(
                        m.getSnapshot().getSnapshotDate(),
                        m.getWordCount(),
                        m.getChangeRatioVsPrev(),
                        m.getRegulatoryDensityIndex(),
                        // simple checksum change proxy
                        m.getChangeRatioVsPrev() != null && Math.abs(m.getChangeRatioVsPrev()) > 0.0
                ))
                .collect(Collectors.toList());

        return new AgencyDetailDto(agency.getId(), agency.getName(), points);
    }*/
}