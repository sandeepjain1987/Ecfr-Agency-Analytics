package gov.usds.ecfr.controller;

import gov.usds.ecfr.dto.AgencyMetricsResponse;
import gov.usds.ecfr.service.AgencyMetricsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/metrics")
public class AgencyMetricsController {

    private final AgencyMetricsService agencyMetricsService;

    public AgencyMetricsController(AgencyMetricsService agencyMetricsService) {
        this.agencyMetricsService = agencyMetricsService;
    }

    @GetMapping("/agency/{agencyId}")
    public AgencyMetricsResponse getAgencyMetrics(@PathVariable Long agencyId) {
        return agencyMetricsService.getMetricsForAgency(agencyId);
    }
}
