package com.kartike.agenticreview.dto;

public class CodeReviewRequest {

    private String code;

    public CodeReviewRequest() {
    }

    public CodeReviewRequest(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
