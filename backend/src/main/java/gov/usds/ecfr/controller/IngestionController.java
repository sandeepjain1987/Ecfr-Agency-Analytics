package gov.usds.ecfr.controller;

import gov.usds.ecfr.service.EcfrIngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ingest")
@RequiredArgsConstructor
//@CrossOrigin
public class IngestionController {

    private final EcfrIngestionService ingestionService;

    @PostMapping("/agency/{id}")
    public ResponseEntity<Void> ingestAgency(@PathVariable Long id) {
         ingestionService.ingestAgency(id);
        return ResponseEntity.accepted().build();
    }
}
