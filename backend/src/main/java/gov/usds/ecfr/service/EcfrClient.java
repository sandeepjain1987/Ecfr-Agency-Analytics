package gov.usds.ecfr.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import gov.usds.ecfr.service.AgencyService;
// TODO: implement actual HTTP calls to eCFR API
@Component
public class EcfrClient implements CommandLineRunner {

    // Example placeholder method
    public String fetchRawData() {
        // TODO: use WebClient or RestTemplate to call eCFR API
        return "";
    }

    private final AgencyService service;

    public EcfrClient(AgencyService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        service.fetchAndStoreAgencies();
    }

}