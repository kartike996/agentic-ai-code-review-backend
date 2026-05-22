package com.kartike.agenticreview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.kartike.agenticreview.dto.CodeReviewRequest;
import com.kartike.agenticreview.dto.CodeReviewResponse;
import com.kartike.agenticreview.service.CodeReviewService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CodeReviewController {

    @Autowired
    private CodeReviewService codeReviewService;

    @PostMapping("/review")
    public CodeReviewResponse reviewCode(
            @RequestBody CodeReviewRequest request) {

        return codeReviewService.reviewCode(
                request.getCode()
        );
    }
    
    @GetMapping("/review/latest-commit")
    public CodeReviewResponse reviewLatestCommit() {

        return codeReviewService.reviewLatestCommit();
    }
}
