package com.kartike.agenticreview.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.concurrent.CompletableFuture;

import com.kartike.agenticreview.dto.CodeReviewResponse;
import com.kartike.agenticreview.dto.GitCommitInfo;


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

        CodeReviewResponse response = reviewCode(gitChanges);

        response.setGitCommitInfo(
                gitService.getLatestCommitInfo()
        );

        return response;
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
    
    public GitCommitInfo getLatestCommitInfo() {

        try {

            ProcessBuilder commitProcess = new ProcessBuilder(
                    "git",
                    "log",
                    "-1",
                    "--pretty=format:%H|%s"
            );

            commitProcess.directory(
                    new java.io.File(System.getProperty("user.dir"))
            );

            Process process = commitProcess.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String commitData = reader.readLine();

            if (commitData == null) {
                return null;
            }

            String[] parts = commitData.split("\\|");

            String commitId = parts[0];
            String commitMessage = parts[1];

            ProcessBuilder filesProcess = new ProcessBuilder(
                    "git",
                    "diff-tree",
                    "--no-commit-id",
                    "--name-only",
                    "-r",
                    "HEAD"
            );

            filesProcess.directory(
                    new java.io.File(System.getProperty("user.dir"))
            );

            Process fileProcess = filesProcess.start();

            BufferedReader fileReader = new BufferedReader(
                    new InputStreamReader(fileProcess.getInputStream())
            );

            java.util.List<String> files = new java.util.ArrayList<>();

            String line;

            while ((line = fileReader.readLine()) != null) {
                files.add(line);
            }

            return new GitCommitInfo(
                    commitId,
                    commitMessage,
                    files
            );

        } catch (Exception e) {

            return null;
        }
    }
}
