package com.kartike.agenticreview.dto;

public class CodeReviewResponse {

    private String qualityReview;
    private String securityReview;
    private String maintainabilityReview;
    private String overallReview;

    public CodeReviewResponse() {
    }

    public CodeReviewResponse(String qualityReview,
                              String securityReview,
                              String maintainabilityReview,
                              String overallReview) {

        this.qualityReview = qualityReview;
        this.securityReview = securityReview;
        this.maintainabilityReview = maintainabilityReview;
        this.overallReview = overallReview;
    }

    public String getQualityReview() {
        return qualityReview;
    }

    public void setQualityReview(String qualityReview) {
        this.qualityReview = qualityReview;
    }

    public String getSecurityReview() {
        return securityReview;
    }

    public void setSecurityReview(String securityReview) {
        this.securityReview = securityReview;
    }

    public String getMaintainabilityReview() {
        return maintainabilityReview;
    }

    public void setMaintainabilityReview(String maintainabilityReview) {
        this.maintainabilityReview = maintainabilityReview;
    }

	public String getOverallReview() {
		return overallReview;
	}

	public void setOverallReview(String overallReview) {
		this.overallReview = overallReview;
	}
}
