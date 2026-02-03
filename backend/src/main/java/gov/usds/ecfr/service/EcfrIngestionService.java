package gov.usds.ecfr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.usds.ecfr.domain.Agency;
import gov.usds.ecfr.domain.Part;
import gov.usds.ecfr.dto.StructureNodeDto;
import gov.usds.ecfr.ecfr.EcfrVersionerClient;
import gov.usds.ecfr.repository.AgencyRepository;
import gov.usds.ecfr.repository.CfrReferenceRepository;
import gov.usds.ecfr.repository.PartRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EcfrIngestionService {

    private final EcfrVersionerClient versionerClient;
    private final CfrStructureService cfrStructureService;
    private final TextAnalysisService textAnalysisService;
    private final AgencyRepository agencyRepository;
    private final PartRepository partRepository;
    private final CfrReferenceRepository cfrReferenceRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void ingestAgency(Long agencyId) {
        System.out.print("Ingestion Started for Agency id:" + agencyId);
        Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new RuntimeException("Agency not found"));

        List<Object[]> rows = cfrReferenceRepository.findTitleAndChapterByAgencyId(agencyId);
        int titleNumber = 0;
        String chapter = "";
        long totalWords = 0L;
        int totalComplexity = 0;
        StringBuilder combinedText = new StringBuilder();

        for (Object[] row : rows) {
            titleNumber = (int) row[0];
            chapter = (String) row[1];
            System.out.print("Processing title: " + titleNumber + " chapter: " + chapter + " for Agency: " + agency.getDisplayName());
            String date = versionerClient.getLatestSnapshotDateForTitle(titleNumber);

            StructureNodeDto title1 = versionerClient.loadStructure(titleNumber);
            StructureNodeDto chapterIII = cfrStructureService.getChapter(title1, chapter);
            List<StructureNodeDto> parts = cfrStructureService.getParts(chapterIII);

            for (int i = 0; i < parts.size(); i++) {
                if (!parts.get(i).getLabel().contains("Reserved")) {
                    String partNumber = parts.get(i).getIdentifier();
                    String titleXml = versionerClient.getTitleXml(date, titleNumber).block();
                    System.out.println("Title XML LENGTH = " + titleXml.length());
                    String partXml = versionerClient.extractPartFromTitleXml(titleXml, partNumber);
                    System.out.println("Processing Part " + partNumber);
                    System.out.println("PART XML LENGTH = " + partXml.length());

                    Part parsed = textAnalysisService.parsePartXml(partXml, titleNumber, partNumber);
                    parsed.setAgency(agency);
                    partRepository.save(parsed);
                    totalWords += parsed.getWordCount();
                    System.out.println("Total words for Part " + partNumber + " is " + totalWords);
                    totalComplexity += parsed.getComplexityScore();
                    combinedText.append(parsed.getText());
                }
            }
        }
        agency.setTotalWordCount(totalWords);
        agency.setComplexityScore(totalComplexity);
        agency.setChecksum(textAnalysisService.checksum(combinedText.toString()));
        agencyRepository.save(agency);

        System.out.print("Ingestion Completed for Agency id:" + agencyId);
    }
}