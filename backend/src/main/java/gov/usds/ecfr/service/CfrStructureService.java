package gov.usds.ecfr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.usds.ecfr.dto.StructureNodeDto;
import gov.usds.ecfr.ecfr.EcfrVersionerClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class CfrStructureService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private  EcfrVersionerClient ecfrVersionerClient;

    public CfrStructureService(WebClient.Builder builder) {
        this.webClient = builder.exchangeStrategies(ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(12 * 1024 * 1024)) // 12 MB
                .build())
                .build();
    }

    // ------------------------------------------------------------
    // 1. Load Title Structure JSON
    // ------------------------------------------------------------


    // ------------------------------------------------------------
    // 2. Get Chapter by Identifier (e.g., "III")
    // ------------------------------------------------------------
    public StructureNodeDto getChapter(StructureNodeDto titleNode, String chapterIdentifier) {
        if (titleNode == null || titleNode.getChildren() == null) {
            return null;
        }

        String target = normalize(chapterIdentifier);

        for (StructureNodeDto child : titleNode.getChildren()) {
            if (!"chapter".equalsIgnoreCase(child.getType()) &&
                    !"chap".equalsIgnoreCase(child.getType())) {
                continue;
            }

            String id = normalize(child.getIdentifier());
            String label = normalize(child.getLabel());

            // Match identifier
            if (id.equalsIgnoreCase(target)) {
                return child;
            }

            // 2. Label pattern match: "CHAPTER V ..." etc.
            if (label.startsWith("CHAPTER " + target + " ")
                    || label.equals("CHAPTER " + target)) {
                return child;
            }

        }

        return null;

    }
    private String normalize(String s) {
        if (s == null) return "";
        return s.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
    }


    // ------------------------------------------------------------
    // 3. Get all Parts under a Chapter (recursive)
    // ------------------------------------------------------------
    public List<StructureNodeDto> getParts(StructureNodeDto chapterNode) {
        List<StructureNodeDto> parts = new ArrayList<>();
        collectParts(chapterNode, parts);
        return parts;
    }

    private void collectParts(StructureNodeDto node, List<StructureNodeDto> parts) {
        if (node == null) return;

        if ("part".equalsIgnoreCase(node.getType())) {
            parts.add(node);
        }

        if (node.getChildren() != null) {
            for (StructureNodeDto child : node.getChildren()) {
                collectParts(child, parts);
            }
        }
    }

    // ------------------------------------------------------------
    // 4. Get all Sections under a Part (recursive)
    // ------------------------------------------------------------
    public List<StructureNodeDto> getSections(StructureNodeDto partNode) {
        List<StructureNodeDto> sections = new ArrayList<>();
        collectSections(partNode, sections);
        return sections;
    }

    private void collectSections(StructureNodeDto node, List<StructureNodeDto> sections) {
        if (node == null) return;

        if ("section".equalsIgnoreCase(node.getType())) {
            sections.add(node);
        }

        if (node.getChildren() != null) {
            for (StructureNodeDto child : node.getChildren()) {
                collectSections(child, sections);
            }
        }
    }

    // ------------------------------------------------------------
    // 5. Convenience: Get Parts for a Title + Chapter
    // ------------------------------------------------------------
    public List<StructureNodeDto> getPartsForChapter(int titleNumber, String chapterIdentifier) {
        StructureNodeDto titleNode = ecfrVersionerClient.loadStructure(titleNumber);
        StructureNodeDto chapterNode = getChapter(titleNode, chapterIdentifier);
        return getParts(chapterNode);
    }

    // ------------------------------------------------------------
    // 6. Convenience: Get Sections for a Title + Chapter + Part
    // ------------------------------------------------------------
    public List<StructureNodeDto> getSectionsForPart(int titleNumber, String chapterIdentifier, String partIdentifier) {
        StructureNodeDto titleNode = ecfrVersionerClient.loadStructure(titleNumber);
        StructureNodeDto chapterNode = getChapter(titleNode, chapterIdentifier);

        List<StructureNodeDto> parts = getParts(chapterNode);

        for (StructureNodeDto part : parts) {
            if (part.getIdentifier().equalsIgnoreCase(partIdentifier)) {
                return getSections(part);
            }
        }

        return List.of();
    }
}
