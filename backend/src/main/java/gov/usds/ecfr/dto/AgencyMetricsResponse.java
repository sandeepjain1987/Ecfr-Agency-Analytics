package gov.usds.ecfr.dto;

public class AgencyMetricsResponse {

    private long agencyId;
    private long totalParts;
    private long totalWords;
    private double totalComplexity;

    public AgencyMetricsResponse(long agencyId, long totalParts, long totalWords, double totalComplexity) {
        this.agencyId = agencyId;
        this.totalParts = totalParts;
        this.totalWords = totalWords;
        this.totalComplexity = totalComplexity;
    }

    public long getAgencyId() { return agencyId; }
    public long getTotalParts() { return totalParts; }
    public long getTotalWords() { return totalWords; }
    public double getTotalComplexity() { return totalComplexity; }
}


