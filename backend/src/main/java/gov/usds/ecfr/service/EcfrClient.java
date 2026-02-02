package gov.usds.ecfr.service;

import gov.usds.ecfr.repository.AgencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import gov.usds.ecfr.service.AgencyService;

@Component
public class EcfrClient implements CommandLineRunner {
    private final AgencyService service;
    @Autowired
    private  AgencyRepository agencyRepository;
    public EcfrClient(AgencyService service) {
        this.service = service;
    }

    @Value("${ecfr.ingest.enabled:true}")
    private boolean ingestEnabled;


    @Override
    public void run(String... args) {
        // GUARD: skip ingestion if data already exists
        if (agencyRepository.count() > 0) {
            System.out.println("Agencies already loaded — skipping ingestion.");
            return;
        }
        System.out.println("No agencies found — running ingestion...");
        service.fetchAndStoreAgencies();
    }
}