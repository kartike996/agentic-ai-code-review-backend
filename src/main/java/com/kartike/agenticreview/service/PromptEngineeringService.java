package com.kartike.agenticreview.service;

import org.springframework.stereotype.Service;

@Service
public class PromptEngineeringService {

	public String buildQualityPrompt(String code) {

	    return """
	            You are a senior software engineer specializing in Java code quality analysis.

	            Analyze the following Java code.

	            Provide output ONLY in this format:

	            Issues:
	            - point
	            - point

	            Recommendations:
	            - point
	            - point

	            Rules:
	            - Keep response concise
	            - Maximum 5 bullet points
	            - Avoid unnecessary explanations

	            Java Code:
	            """ + code;
	}

	public String buildSecurityPrompt(String code) {

	    return """
	            You are a cybersecurity expert performing secure code review.

	            Analyze the following Java code.

	            Provide output ONLY in this format:

	            Vulnerabilities:
	            - point
	            - point

	            Recommendations:
	            - point
	            - point

	            Rules:
	            - Keep response concise
	            - Maximum 5 bullet points
	            - Avoid unnecessary explanations

	            Java Code:
	            """ + code;
	}

	public String buildMaintainabilityPrompt(String code) {

	    return """
	            You are a software architect evaluating maintainability.

	            Analyze the following Java code.

	            Provide output ONLY in this format:

	            Concerns:
	            - point
	            - point

	            Refactoring Suggestions:
	            - point
	            - point

	            Rules:
	            - Keep response concise
	            - Maximum 5 bullet points
	            - Avoid unnecessary explanations

	            Java Code:
	            """ + code;
	}
}
