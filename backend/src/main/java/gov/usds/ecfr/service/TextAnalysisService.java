package gov.usds.ecfr.service;

import gov.usds.ecfr.domain.Part;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;

@Service
public class TextAnalysisService {

    /**
     * Computes the number of words in the given text.
     */
    public long wordCount(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        // Normalize all whitespace to single spaces
        String normalized = text
                .replaceAll("\\s+", " ")   // collapse whitespace
                .trim();

        // Split on spaces only (not punctuation)
        String[] tokens = normalized.split(" ");

        // Filter out empty tokens (defensive)
        int count = 0;
        for (String token : tokens) {
            if (!token.isBlank()) {
                count++;
            }
        }

        return count;

    }

    /**
     * Computes a SHA-256 checksum of the text.
     */
    public String checksum(String text) {
        if (text == null) text = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException("Failed to compute checksum", e);
        }
    }

    /**
     * Computes a custom complexity score based on:
     * - Number of sentences
     * - Occurrences of "must", "shall", "required"
     */
    public int complexityScore(String text) {
        if (text == null || text.isBlank()) return 0;

        String normalized = text.toLowerCase(Locale.ROOT);

        int sentences = normalized.split("[.!?]").length;
        int must = countOccurrences(normalized, " must ");
        int shall = countOccurrences(normalized, " shall ");
        int required = countOccurrences(normalized, " required ");

        return sentences + must + shall + required;
    }

    /**
     * Counts occurrences of a token inside text.
     */
    private int countOccurrences(String text, String token) {
        int count = 0;
        int idx = 0;

        while ((idx = text.indexOf(token, idx)) != -1) {
            count++;
            idx += token.length();
        }

        return count;
    }

    public Part parsePartXml(String xml, int titleNumber, String partNumber)  {

        if (xml == null || xml.isBlank()) {
            throw new IllegalStateException("Versioner returned empty XML");
        }

        // Basic sanity check: must contain at least one SECTION and one P
        if (!xml.contains("TYPE=\"SECTION\"") || !xml.contains("<P")) {
            throw new IllegalStateException("Extracted part XML is incomplete");
        }

        // Parse DOM
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setIgnoringComments(true);
        factory.setCoalescing(true);

        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        Document doc;
        try {
            doc = builder.parse(new InputSource(new StringReader(xml)));
        } catch (Exception e) {
            throw new IllegalStateException("XML parsing failed — malformed XML", e);
        }

        // DOM structural validation
        if (doc.getElementsByTagName("DIV8").getLength() == 0 &&
                doc.getElementsByTagName("DIV6").getLength() == 0) {
            throw new IllegalStateException("Parsed XML contains no SECTION elements");
        }

        if (doc.getElementsByTagName("P").getLength() == 0) {
            throw new IllegalStateException("Parsed XML contains no paragraph elements");
        }

        // Extract text from the DOM subtree
        String text;
        try {
            text = extractCfrText(xml);   // <-- FIXED: pass original XML, not doc.toString()
        } catch (Exception e) {
            throw new RuntimeException("Text extraction failed", e);
        }

        // Compute metrics
        long wordCnt = wordCount(text);
        String checksum = checksum(text);
        Integer complexity = complexityScore(text);

        // Build Part entity
        Part part = new Part();
        part.setTitleNumber(titleNumber);
        part.setPartNumber(partNumber);
        part.setText(text);
        part.setWordCount(wordCnt);
        part.setChecksum(checksum);
        part.setComplexityScore(complexity);

        return part;
    }
    public String extractCfrText(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setIgnoringComments(true);
        factory.setCoalescing(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(xml)));

        StringBuilder sb = new StringBuilder();
        extractNodes(doc.getDocumentElement(), sb);
        return sb.toString().trim();
    }

    private void extractNodes(Node node, StringBuilder sb) {
        String name = node.getNodeName();

        switch (name) {
            case "HEAD":
            case "P":
            case "FP":
            case "EDNOTE":
                append(sb, node.getTextContent());
                break;

            case "STARS":
                append(sb, "* * *");
                break;
        }

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            extractNodes(children.item(i), sb);
        }
    }

    private void append(StringBuilder sb, String text) {
        if (text != null && !text.isBlank()) {
            sb.append(text.trim()).append("\n\n");
        }
    }

    public String validateAndExtract(String xml) throws Exception {

        // 1. Basic checks
        if (xml == null || xml.isBlank()) {
            throw new IllegalStateException("Versioner returned empty XML");
        }

        // 2. Structural checks
        if (!xml.contains("<SECTION") || !xml.contains("<P")) {
            throw new IllegalStateException("Versioner returned incomplete CFR XML");
        }

        // 3. Parse with DOM
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setIgnoringComments(true);
        factory.setCoalescing(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc;

        try {
            doc = builder.parse(new InputSource(new StringReader(xml)));
        } catch (Exception e) {
            throw new IllegalStateException("XML parsing failed — malformed XML", e);
        }

        // 4. DOM structural validation
        if (doc.getElementsByTagName("SECTION").getLength() == 0) {
            throw new IllegalStateException("Parsed XML contains no SECTION elements");
        }

        if (doc.getElementsByTagName("P").getLength() < 10) {
            throw new IllegalStateException("Parsed XML contains too few P elements — XML is truncated");
        }

        // 5. Extract text
        return extractCfrText(String.valueOf(doc));
    }

    /*public String extractCfrText(Document doc) {
        StringBuilder sb = new StringBuilder();

       // HEAD (section headings)
        doc.select("HEAD, head").forEach(head -> {
            appendClean(sb, head.text());
        });

        // Paragraphs <P> (uppercase or lowercase)
        doc.select("P, p").forEach(p -> {
            appendClean(sb, p.text());
            System.out.println("P-TAG: " + p.text());
            System.out.println("P count = " + doc.select("P, p").size());

        });


        // Stars <STARS/> (uppercase or lowercase)
        doc.select("STARS, stars").forEach(stars -> {
            appendClean(sb, "* * *");
        });

        // Footnotes <FP>
        doc.select("FP, fp").forEach(fp -> {
            appendClean(sb, fp.text());
        });

        // Editorial notes <EDNOTE>
        doc.select("EDNOTE, ednote").forEach(ed -> {
            appendClean(sb, ed.text());
        });



        return sb.toString().trim();

    }
    private void appendClean(StringBuilder sb, String text) {
        if (text != null && !text.isBlank()) {
            sb.append(text.trim()).append("\n\n");
        }
    }*/


}
