package com.kartike.agenticreview.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;

import com.kartike.agenticreview.dto.GitCommitInfo;

@Service
public class GitService {

    public String getLatestCommitChanges() {

        StringBuilder output = new StringBuilder();

        try {

            ProcessBuilder processBuilder = new ProcessBuilder(
            		"C:\\Users\\Guest user\\AppData\\Local\\Programs\\Git\\cmd\\git.exe",
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
    
    public GitCommitInfo getLatestCommitInfo() {

        try {

        	ProcessBuilder idProcess = new ProcessBuilder(
        			"C:\\Users\\Guest user\\AppData\\Local\\Programs\\Git\\cmd\\git.exe",
        		    "rev-parse",
        		    "HEAD"
        		);

            idProcess.directory(
                    new java.io.File(System.getProperty("user.dir"))
            );

            Process process1 = idProcess.start();

            BufferedReader reader1 = new BufferedReader(
                    new InputStreamReader(process1.getInputStream())
            );

            String commitId = reader1.readLine();

            ProcessBuilder messageProcess = new ProcessBuilder(
            		"C:\\Users\\Guest user\\AppData\\Local\\Programs\\Git\\cmd\\git.exe",
                    "log",
                    "-1",
                    "--pretty=%B"
            );

            messageProcess.directory(
                    new java.io.File(System.getProperty("user.dir"))
            );

            Process process2 = messageProcess.start();

            BufferedReader reader2 = new BufferedReader(
                    new InputStreamReader(process2.getInputStream())
            );

            String commitMessage = reader2.readLine();

            ProcessBuilder filesProcess = new ProcessBuilder(
            		"C:\\Users\\Guest user\\AppData\\Local\\Programs\\Git\\cmd\\git.exe",
                    "diff-tree",
                    "--no-commit-id",
                    "--name-only",
                    "-r",
                    "HEAD"
            );

            filesProcess.directory(
                    new java.io.File(System.getProperty("user.dir"))
            );

            Process process3 = filesProcess.start();

            BufferedReader reader3 = new BufferedReader(
                    new InputStreamReader(process3.getInputStream())
            );

            java.util.List<String> files = new java.util.ArrayList<>();

            String line;

            while ((line = reader3.readLine()) != null) {
                files.add(line);
            }

            return new GitCommitInfo(
                    commitId,
                    commitMessage,
                    files
            );

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }
}