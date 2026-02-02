package gov.usds.ecfr.ecfr;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.usds.ecfr.dto.StructureNode;
import gov.usds.ecfr.dto.TitleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import reactor.core.publisher.Mono;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
//@RequiredArgsConstructor
public class EcfrVersionerClient {
    private final ObjectMapper objectMapper = new ObjectMapper();
    WebClient webClient;
    public EcfrVersionerClient() {
        //System.out.println("EcfrVersionerClient constructor USED");

        this.webClient = WebClient.builder().baseUrl("https://www.ecfr.gov/api/versioner/v1")
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(100 * 1024 * 1024)) // 100 MB
                        .build()).build();
    }
    /*private final WebClient webClient = WebClient.builder()
            .baseUrl("https://www.ecfr.gov/api/versioner/v1")
            .exchangeStrategies(ExchangeStrategies.builder()
                    .codecs(configurer -> configurer
                            .defaultCodecs()
                            .maxInMemorySize(10 * 1024 * 1024)) // 10 MB
                    .build())

//            .filter(logRequest())
//            .filter(logResponse())
            .build();*/

    public Mono<String> getTitleStructure(String date, int title) {
        return webClient.get()
                .uri("/structure/{date}/title-{title}.json", date, title)
                .retrieve()
                .bodyToMono(String.class);
    }
    public Mono<String> getTitleXml(String date, int title) {
        return webClient.get()
                .uri("/full/{date}/title-{title}?format=xml", date, title)
                .header("Accept", "application/xml")
                .header("User-Agent", "Mozilla/5.0")



                .retrieve()
                .bodyToMono(String.class);
    }


    public String getLatestSnapshotDateForTitle(int titleNumber) {
        JsonNode json = webClient.get()
                .uri("/titles.json")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        for (JsonNode t : json.get("titles")) {
            if (t.get("number").asInt() == titleNumber) {
                return t.get("up_to_date_as_of").asText();
            }
        }

        throw new IllegalArgumentException("Title " + titleNumber + " not found in Versioner API");
    }
    public Mono<String> getPartXml(String date, int title, String part) {
        return webClient.get()
                .uri("/full/{date}/title-{title}.xml?part={part}", date, title, part)
                .retrieve()
                .bodyToMono(String.class);


    }
    public Mono<String> getPartXmlFromChapter(String date, int title, String chapterNo) {

        String url = String.format(
                "https://www.ecfr.gov/api/versioner/v1/full/%s/title-%d.xml?chapter=%s",
                date, title, chapterNo
        );

        return fetchXml(url);

    }



    public List<TitleDto> getAvailableTitles() {
        return webClient.get()
                .uri("/titles.json")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> {
                    List<TitleDto> list = new ArrayList<>();
                    json.get("titles").forEach(t -> {
                        list.add(new TitleDto(
                                t.get("number").asInt(),
                                t.get("name").asText()
                        ));
                    });
                    return list;
                })
                .block();
    }



    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            System.out.println("➡️ WebClient Request: " + request.method() + " " + request.url());
            return Mono.just(request);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            System.out.println("⬅️ WebClient Response: " + response.statusCode());
            return Mono.just(response);
        });
    }

    public Mono<String> fetchXml(String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return new String(bytes, StandardCharsets.UTF_8);
                })
                .reduce(String::concat);
    }

    public StructureNode loadStructure(int titleNumber) {
        String date =  getLatestSnapshotDateForTitle(titleNumber);
        String url = String.format(
                "https://www.ecfr.gov/api/versioner/v1/structure/%s/title-%d.json",
                date, titleNumber
        );

        return webClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, StructureNode.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })

                .block();
    }

    public String extractPartFromTitleXml(String titleXml, String partNumber) {
        try {

            // 1. Build DOM
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setIgnoringComments(true);
            factory.setCoalescing(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(titleXml)));

            // 2. Find the <DIV5 TYPE="PART" N="301"> node
            NodeList parts = doc.getElementsByTagName("DIV5");
            Element partNode = null;

            for (int i = 0; i < parts.getLength(); i++) {
                //System.out.println("Found DIV5 count = " + parts.getLength());
                Element el = (Element) parts.item(i);
                //System.out.println("DIV5 N=\"" + el.getAttribute("N") + "\" TYPE=\"" + el.getAttribute("TYPE") + "\"");

                if ("PART".equals(el.getAttribute("TYPE"))
                        && partNumber.equals(el.getAttribute("N"))) {
                    partNode = el;
                    break;
                }
            }

            if (partNode == null) {
                throw new IllegalStateException("Part " + partNumber + " not found in Title XML");
            }

            // 3. Convert the <DIV5> subtree back to XML string
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(partNode), new StreamResult(writer));

            return writer.toString();

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract part " + partNumber + " from Title XML", e);
        }
    }


}
