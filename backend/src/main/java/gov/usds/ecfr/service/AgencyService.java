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
    private final RestTemplate rest = new RestTemplate();

    public AgencyService(AgencyRepository agencyRepository) {
        this.agencyRepository = agencyRepository;
    }

    public void fetchAndStoreAgencies() {
        String url = "https://www.ecfr.gov/api/admin/v1/agencies.json";

        AgenciesResponse response = rest.getForObject(url, AgenciesResponse.class);
        if (response == null || response.getAgencies() == null) return;

        response.getAgencies().forEach(dto -> {
            //Agency parent = new Agency();
            // 1. Find existing agency by NAME
            Agency parent = agencyRepository.findByName(dto.getName())
                    .orElseGet(Agency::new);

            parent.setName(dto.getName());
            parent.setShortName(dto.getShortName());
            parent.setDisplayName(dto.getDisplayName());
            parent.setSortableName(dto.getSortableName());
            parent.setSlug(dto.getSlug());

            List<CfrReferenceEntity> cfrrefs = dto.getCfrReferences().stream().map(refDto -> {
                CfrReferenceEntity cfrReferenceEntity = new CfrReferenceEntity();
                cfrReferenceEntity.setTitle(refDto.getTitle());
                cfrReferenceEntity.setChapter(refDto.getChapter());
                cfrReferenceEntity.setPart(refDto.getPart());
                cfrReferenceEntity.setSubtitle(refDto.getSubtitle());
                cfrReferenceEntity.setAgency(parent);
                return cfrReferenceEntity;
            }).toList();

            List<Agency> childrenRefs = dto.getChildren().stream().map(refDto -> {
                Agency agencyRef = new Agency();
                agencyRef.setName(refDto.getName());
                agencyRef.setShortName(dto.getShortName());
                agencyRef.setDisplayName(dto.getDisplayName());
                agencyRef.setSortableName(dto.getSortableName());
                agencyRef.setSlug(dto.getSlug());
                return agencyRef;
            }).toList();

            parent.setCfrReferences(cfrrefs);
            parent.setChildren(childrenRefs);
            agencyRepository.save(parent);
        });
    }
}