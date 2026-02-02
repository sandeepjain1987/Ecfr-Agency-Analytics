package gov.usds.ecfr.dto;

//Used by FE to show Pie chart
public class AgencySummary {
    private Long agencyId;
    private String name;
    private long totalWords;


    public AgencySummary(Long agencyId, String name, long totalWords) {
        this.agencyId = agencyId;
        this.name = name;
        this.totalWords = totalWords;
    }

    public Long getAgencyId() { return agencyId; }
    public String getName() { return name; }
    public long getTotalWords() { return totalWords; }
}
