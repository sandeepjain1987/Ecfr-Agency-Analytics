package gov.usds.ecfr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.usds.ecfr.domain.Agency;
import gov.usds.ecfr.domain.CfrReferenceEntity;
import gov.usds.ecfr.domain.Part;
import gov.usds.ecfr.dto.StructureNode;
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
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import java.io.StringReader;
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

    /**
     * Ingests a single title for a given date.
     * Example: date = "2025-01-30", title = 1
     */
    @Transactional
    public void ingestTitle(int titleNumber) {
        String date = versionerClient.getLatestSnapshotDateForTitle(titleNumber);

        String structureJson = versionerClient.getTitleStructure(date, titleNumber)
                .blockOptional()
                .orElseThrow(() -> new IllegalStateException("No structure JSON returned"));

        JsonNode root;
        try {
            root = objectMapper.readTree(structureJson);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse structure JSON", e);
        }

        // The structure JSON has a "children" array starting at title level
        List<PartNode> parts = new ArrayList<>();
        collectParts(root.get("children"), titleNumber, parts);

        String agencyName = "Administrative Conference of the United States";
        Agency agency = agencyRepository.findByName(agencyName)
                .orElseGet(() -> agencyRepository.save(
                        Agency.builder()
                                .name(agencyName)
                                .displayName(agencyName)
                                .totalWordCount(0L)
                                .complexityScore(0)
                                .checksum(null)
                                .build()
                ));

        long totalWords = 0L;
        int totalComplexity = 0;
        StringBuilder combinedText = new StringBuilder();

        for (PartNode partNode : parts) {
            String xml = versionerClient.getPartXml(date, titleNumber, partNode.partNumber())
                    .blockOptional()
                    .orElse("");

            String text = extractPlainTextFromXml(xml);

            long wc = textAnalysisService.wordCount(text);
            int cs = textAnalysisService.complexityScore(text);
            String hash = textAnalysisService.checksum(text);

            Part part = Part.builder()
                    .titleNumber(titleNumber)
                    .partNumber(partNode.partNumber())
                    .text(text)
                    .wordCount(wc)
                    .complexityScore(cs)
                    .agency(agency)
                    .build();
            partRepository.save(part);

            totalWords += wc;
            totalComplexity += cs;
            combinedText.append(text);
        }

        agency.setTotalWordCount(totalWords);
        agency.setComplexityScore(totalComplexity);
        agency.setChecksum(textAnalysisService.checksum(combinedText.toString()));
        agencyRepository.save(agency);
    }
    @Transactional
    public void ingestAgency(Long agencyId) {
        System.out.print("Ingestion Started for Agency id:"+ agencyId);
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
            System.out.print("Processing title: "+ titleNumber + " chapter: "+ chapter+ " for Agency: "+ agency.getDisplayName());
            String date = versionerClient.getLatestSnapshotDateForTitle(titleNumber);

            StructureNode title1 = versionerClient.loadStructure(titleNumber);
            StructureNode chapterIII = cfrStructureService.getChapter(title1, chapter);
            List<StructureNode> parts = cfrStructureService.getParts(chapterIII);



            for (int i = 0; i < parts.size(); i++) {
                if (!parts.get(i).getLabel().contains("Reserved")) {

                    String partNumber = parts.get(i).getIdentifier();

                    String titleXml = versionerClient.getTitleXml(date, titleNumber).block();
                    // System.out.println(titleXml.substring(0, 200));
                    System.out.println("Title XML LENGTH = " + titleXml.length());
                    String partXml = versionerClient.extractPartFromTitleXml(titleXml, partNumber);
                    System.out.println("Processing Part " + partNumber);
                    System.out.println("PART XML LENGTH = " + partXml.length());

                    Part parsed = textAnalysisService.parsePartXml(partXml, titleNumber, partNumber);
                    parsed.setAgency(agency);
                    partRepository.save(parsed);
                    totalWords += parsed.getWordCount();
                    System.out.println("Total words for Part " + partNumber+ " is "+ totalWords);
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

   /* @Transactional
    public void ingestAgency(Long agencyId) {

        Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new RuntimeException("Agency not found"));

        List<Object[]> rows = cfrReferenceRepository.findTitleAndChapterByAgencyId(agencyId);

        long totalWords = 0L;
        int totalComplexity = 0;
        StringBuilder combinedText = new StringBuilder();

        for (Object[] row : rows) {

            int titleNumber = (int) row[0];
            String chapter = (String) row[1];

            String date = versionerClient.getLatestSnapshotDateForTitle(titleNumber);

            StructureNode title1 = versionerClient.loadStructure(titleNumber);
            StructureNode chapterNode = cfrStructureService.getChapter(title1, chapter);
            List<StructureNode> parts = cfrStructureService.getParts(chapterNode);

            for (StructureNode partNode : parts) {

                if (!partNode.getLabel().contains("Reserved")) {

                    String partNumber = partNode.getIdentifier();

                    String titleXml = versionerClient.getTitleXml(date, titleNumber).block();
                    String partXml = versionerClient.extractPartFromTitleXml(titleXml, partNumber);

                    Part parsed = textAnalysisService.parsePartXml(partXml, titleNumber, partNumber);
                    parsed.setAgency(agency);
                    partRepository.save(parsed);

                    totalWords += parsed.getWordCount();
                    totalComplexity += parsed.getComplexityScore();
                    combinedText.append(parsed.getText());
                }
            }
        }

        // FINAL TOTALS FOR THE ENTIRE AGENCY
        agency.setTotalWordCount(totalWords);
        agency.setComplexityScore(totalComplexity);
        agency.setChecksum(textAnalysisService.checksum(combinedText.toString()));

        agencyRepository.save(agency);

        System.out.println("Ingestion Completed for Agency id:" + agencyId);
    }*/


    /**
     * Recursively walks the structure tree and collects all "part" nodes.
     */
    private void collectParts(JsonNode childrenNode, int titleNumber, List<PartNode> parts) {
        if (childrenNode == null || !childrenNode.isArray()) return;

        for (JsonNode node : childrenNode) {

            String type = node.path("type").asText();   // <-- correct field

            if ("part".equalsIgnoreCase(type) && !node.path("reserved").asBoolean()) {

                String partNumber = node.path("identifier").asText();  // <-- correct field
                String heading = node.path("label_description").asText(); // <-- correct field

                parts.add(new PartNode(titleNumber, partNumber, heading));
            }

            JsonNode childChildren = node.get("children");
            if (childChildren != null && childChildren.isArray()) {
                collectParts(childChildren, titleNumber, parts);
            }
        }
    }

    /**
     * Extracts plain text from the eCFR XML using Jsoup.
     */
    private String extractPlainTextFromXml(String xml) {
        if (xml == null || xml.isBlank()) return "";
        try {
            Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
            // eCFR XML has many tags; simplest is to grab all text
            return doc.text();
        } catch (Exception e) {
            // Fallback: return raw XML if parsing fails
            return xml;
        }
    }

    private record PartNode(int titleNumber, String partNumber, String heading) {}



    public StructureNode getChapter(StructureNode titleNode, String chapterIdentifier) {
        if (titleNode == null || titleNode.getChildren() == null) {
            return null;
        }

        for (StructureNode child : titleNode.getChildren()) {
            if ("chapter".equalsIgnoreCase(child.getType())) {

                // Match by identifier (I, II, III, etc.)
                if (child.getIdentifier().equalsIgnoreCase(chapterIdentifier)) {
                    return child;
                }

                // Optional: match by label ("Chapter I", "Chapter II", etc.)
                if (child.getLabel() != null &&
                        child.getLabel().toLowerCase().contains(chapterIdentifier.toLowerCase())) {
                    return child;
                }
            }
        }

        return null;
    }



}