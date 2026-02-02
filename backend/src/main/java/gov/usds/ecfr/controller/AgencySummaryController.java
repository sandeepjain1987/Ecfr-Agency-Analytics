package gov.usds.ecfr.controller;

import gov.usds.ecfr.dto.AgencySummary;
import gov.usds.ecfr.service.AgencySummaryService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/metrics")
@CrossOrigin(origins = "http://localhost:5173")
public class AgencySummaryController {

    private final AgencySummaryService service;

    public AgencySummaryController(AgencySummaryService service) {
        this.service = service;
    }

    @GetMapping("/agencies")
    public List<AgencySummary> getAllAgencyMetrics() {
        return service.getAllAgencySummaries();
    }
}
