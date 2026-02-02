package gov.usds.ecfr.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.usds.ecfr.dto.StructureNode;
import gov.usds.ecfr.ecfr.EcfrVersionerClient;
import org.springframework.http.MediaType;
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
    public StructureNode getChapter(StructureNode titleNode, String chapterIdentifier) {
        if (titleNode == null || titleNode.getChildren() == null) {
            return null;
        }

        String target = normalize(chapterIdentifier);

        for (StructureNode child : titleNode.getChildren()) {
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
    public List<StructureNode> getParts(StructureNode chapterNode) {
        List<StructureNode> parts = new ArrayList<>();
        collectParts(chapterNode, parts);
        return parts;
    }

    private void collectParts(StructureNode node, List<StructureNode> parts) {
        if (node == null) return;

        if ("part".equalsIgnoreCase(node.getType())) {
            parts.add(node);
        }

        if (node.getChildren() != null) {
            for (StructureNode child : node.getChildren()) {
                collectParts(child, parts);
            }
        }
    }

    // ------------------------------------------------------------
    // 4. Get all Sections under a Part (recursive)
    // ------------------------------------------------------------
    public List<StructureNode> getSections(StructureNode partNode) {
        List<StructureNode> sections = new ArrayList<>();
        collectSections(partNode, sections);
        return sections;
    }

    private void collectSections(StructureNode node, List<StructureNode> sections) {
        if (node == null) return;

        if ("section".equalsIgnoreCase(node.getType())) {
            sections.add(node);
        }

        if (node.getChildren() != null) {
            for (StructureNode child : node.getChildren()) {
                collectSections(child, sections);
            }
        }
    }

    // ------------------------------------------------------------
    // 5. Convenience: Get Parts for a Title + Chapter
    // ------------------------------------------------------------
    public List<StructureNode> getPartsForChapter(int titleNumber, String chapterIdentifier) {
        StructureNode titleNode = ecfrVersionerClient.loadStructure(titleNumber);
        StructureNode chapterNode = getChapter(titleNode, chapterIdentifier);
        return getParts(chapterNode);
    }

    // ------------------------------------------------------------
    // 6. Convenience: Get Sections for a Title + Chapter + Part
    // ------------------------------------------------------------
    public List<StructureNode> getSectionsForPart(int titleNumber, String chapterIdentifier, String partIdentifier) {
        StructureNode titleNode = ecfrVersionerClient.loadStructure(titleNumber);
        StructureNode chapterNode = getChapter(titleNode, chapterIdentifier);

        List<StructureNode> parts = getParts(chapterNode);

        for (StructureNode part : parts) {
            if (part.getIdentifier().equalsIgnoreCase(partIdentifier)) {
                return getSections(part);
            }
        }

        return List.of();
    }
}
