package com.kartike.agenticreview.dto;

import java.util.List;

public class GitCommitInfo {

    private String commitId;
    private String commitMessage;
    private List<String> changedFiles;

    public GitCommitInfo() {
    }

    public GitCommitInfo(
            String commitId,
            String commitMessage,
            List<String> changedFiles
    ) {
        this.commitId = commitId;
        this.commitMessage = commitMessage;
        this.changedFiles = changedFiles;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }

    public List<String> getChangedFiles() {
        return changedFiles;
    }

    public void setChangedFiles(List<String> changedFiles) {
        this.changedFiles = changedFiles;
    }
}