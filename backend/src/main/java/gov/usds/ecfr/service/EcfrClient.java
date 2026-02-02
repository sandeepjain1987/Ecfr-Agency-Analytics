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
        try {
            // Try a lightweight query to check if the table exists
            agencyRepository.count();
        } catch (Exception e) {
            System.out.println("Schema not ready — skipping ingestion on startup.");
            return;
        }

        // If we reach here, the table exists
        long count = agencyRepository.count();
        if (count > 0) {
            System.out.println("Agencies already loaded — skipping ingestion.");
            return;
        }

        System.out.println("Starting ingestion...");
        service.fetchAndStoreAgencies();

    }
}