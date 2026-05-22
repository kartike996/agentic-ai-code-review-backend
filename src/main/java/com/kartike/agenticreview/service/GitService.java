package com.kartike.agenticreview.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;

@Service
public class GitService {

    public String getLatestCommitChanges() {

        StringBuilder output = new StringBuilder();

        try {

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "git",
                    "diff",
                    "HEAD~1",
                    "HEAD"
            );

            processBuilder.directory(
                    new java.io.File(System.getProperty("user.dir"))
            );

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;

            while ((line = reader.readLine()) != null) {

                output.append(line).append("\n");
            }

            process.waitFor();

        } catch (Exception e) {

            return "Error reading git diff: " + e.getMessage();
        }

        return output.toString();
    }
}