package gov.usds.ecfr.controller;

import gov.usds.ecfr.domain.Agency;
import gov.usds.ecfr.repository.AgencyRepository;
import org.springframework.web.bind.annotation.*;
//import gov.usds.ecfr.repository;

import java.util.List;

@RestController
@RequestMapping("/api/agencies")
//@CrossOrigin // allow frontend dev server
public class AgencyController {

//    private final AgencyService agencyService;
    private final AgencyRepository agencyRepository;

    public AgencyController(AgencyRepository agencyRepository) {
        this.agencyRepository = agencyRepository;
    }

  /*  public AgencyController(AgencyService agencyService) {
        this.agencyService = agencyService;
    }

    public AgencyController(AgencyService agencyService, AgencyRepository agencyRepository) {
        this.agencyService = agencyService;
        this.agencyRepository = agencyRepository;
    }
*/

  /*  @GetMapping
    public List<AgencySummaryDto> listAgencies(
            @RequestParam(value = "snapshotDate", required = false) String snapshotDateStr) {

        LocalDate snapshotDate = snapshotDateStr != null ? LocalDate.parse(snapshotDateStr) : null;
        return agencyService.getAgenciesForSnapshot(snapshotDate);
    }

    @GetMapping("/{id}")
    public AgencyDetailDto getAgency(@PathVariable Long id) {
        return agencyService.getAgencyDetail(id);
    }*/
    @GetMapping
    public List<Agency> getAll() {
        return agencyRepository.findAll();
    }

}