package com.kartike.agenticreview.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.concurrent.CompletableFuture;

import com.kartike.agenticreview.dto.CodeReviewResponse;


@Service
public class CodeReviewService {

    @Autowired
    private WebClient webClient;
    
    private PromptEngineeringService promptEngineeringService;
    private final GitService gitService;

    @Value("${groq.api.key}")
    private String apiKey;
    
    public CodeReviewService(
            PromptEngineeringService promptEngineeringService,
            GitService gitService
    ) {

        this.promptEngineeringService = promptEngineeringService;
        this.gitService = gitService;
    }
    
    public CodeReviewResponse reviewLatestCommit() {

        String gitChanges = gitService.getLatestCommitChanges();

        if (gitChanges.isBlank()) {

            return new CodeReviewResponse(
                    "No changes found.",
                    "No changes found.",
                    "No changes found.",
                    "No Git commit differences detected."
            );
        }

        return reviewCode(gitChanges);
    }

    public CodeReviewResponse reviewCode(String code) {

        CompletableFuture<String> qualityFuture =
                CompletableFuture.supplyAsync(() ->
                        callGroqApi(
                                promptEngineeringService
                                        .buildQualityPrompt(code)
                        )
                );

        CompletableFuture<String> securityFuture =
                CompletableFuture.supplyAsync(() ->
                        callGroqApi(
                                promptEngineeringService
                                        .buildSecurityPrompt(code)
                        )
                );

        CompletableFuture<String> maintainabilityFuture =
                CompletableFuture.supplyAsync(() ->
                        callGroqApi(
                                promptEngineeringService
                                        .buildMaintainabilityPrompt(code)
                        )
                );

        String qualityReview = qualityFuture.join();
        String securityReview = securityFuture.join();
        String maintainabilityReview = maintainabilityFuture.join();

        String overallReview = callGroqApi(
                """
                You are a senior software architect.

                Summarize the following reviews.

                Provide output ONLY in this format:

                Final Assessment:
                - point
                - point

                Rules:
                - Keep response concise
                - Maximum 5 bullet points
                - Avoid unnecessary explanations

                Quality Review:
                """ + qualityReview +

                """

                Security Review:
                """ + securityReview +

                """

                Maintainability Review:
                """ + maintainabilityReview
        );

        return new CodeReviewResponse(
                qualityReview,
                securityReview,
                maintainabilityReview,
                overallReview
        );
    }

    private String callGroqApi(String prompt) {

        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", new Object[] {
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                }
        );

        Map response = webClient.post()
                .uri("https://api.groq.com/openai/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .map(RuntimeException::new)
                )
                .bodyToMono(Map.class)
                .block();

        try {

            var choices = (java.util.List<Map>) response.get("choices");
            var message = (Map) choices.get(0).get("message");

            return message.get("content").toString();

        } catch (Exception e) {
            return "Error generating AI review.";
        }
    }
}
