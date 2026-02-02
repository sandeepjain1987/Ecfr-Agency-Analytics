package gov.usds.ecfr.controller;

import gov.usds.ecfr.domain.Agency;
import gov.usds.ecfr.repository.AgencyRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/agencies")
public class AgencyController {

    private final AgencyRepository agencyRepository;

    public AgencyController(AgencyRepository agencyRepository) {
        this.agencyRepository = agencyRepository;
    }

    @GetMapping
    public List<Agency> getAll() {
        return agencyRepository.findAll();
    }

}