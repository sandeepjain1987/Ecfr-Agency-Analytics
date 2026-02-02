package gov.usds.ecfr.service;

import gov.usds.ecfr.domain.Agency;
import gov.usds.ecfr.domain.CfrReferenceEntity;
import gov.usds.ecfr.repository.AgencyRepository;
import jakarta.transaction.Transactional;
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
    /*@Transactional
    public void fetchAndStoreAgencies() {

        String url = "https://www.ecfr.gov/api/admin/v1/agencies.json";
        AgenciesResponse response = rest.getForObject(url, AgenciesResponse.class);

        if (response == null || response.getAgencies() == null) {
            return;
        }

        response.getAgencies().forEach(dto -> {

            // 1. Find existing agency by NAME
            Agency parent = agencyRepository.findByName(dto.getName())
                    .orElseGet(Agency::new);

            // 2. Update fields
            parent.setName(dto.getName());
            parent.setShortName(dto.getShortName());
            parent.setDisplayName(dto.getDisplayName());
            parent.setSortableName(dto.getSortableName());
            parent.setSlug(dto.getSlug());

            // 3. CFR references
            parent.getCfrReferences().clear();
            parent.getCfrReferences().addAll(
                    dto.getCfrReferences().stream().map(refDto -> {
                        CfrReferenceEntity ref = new CfrReferenceEntity();
                        ref.setTitle(refDto.getTitle());
                        ref.setChapter(refDto.getChapter());
                        ref.setPart(refDto.getPart());
                        ref.setSubtitle(refDto.getSubtitle());
                        ref.setAgency(parent);
                        return ref;
                    }).toList()
            );

            // 4. Children — IMPORTANT: use childDto fields, not parent fields
            parent.getChildren().clear();
            parent.getChildren().addAll(
                    dto.getChildren().stream().map(childDto -> {
                        Agency child = agencyRepository.findByName(childDto.getName())
                                .orElseGet(Agency::new);

                        child.setName(childDto.getName());
                        child.setShortName(childDto.getShortName());
                        child.setDisplayName(childDto.getDisplayName());
                        child.setSortableName(childDto.getSortableName());
                        child.setSlug(childDto.getSlug());
                        child.setParent(parent);

                        return child;
                    }).toList()
            );

            // 5. Save
            agencyRepository.save(parent);
        });
    }*/
}