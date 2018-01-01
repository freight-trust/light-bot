package com.networknt.bot.dockerhub;

import com.networknt.bot.core.Command;
import com.networknt.bot.core.Constants;
import com.networknt.bot.core.RegexReplacement;
import com.networknt.bot.core.cmd.*;
import com.networknt.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DockerhubCommand implements Command {
    private static final Logger logger = LoggerFactory.getLogger(DockerhubCommand.class);
    private static final String CONFIG_NAME = "dockerhub";
    private Map<String, Object> config = Config.getInstance().getJsonMapConfig(CONFIG_NAME);
    private String workspace = (String)config.get(Constants.WORKSPACE);
    private String version = (String)config.get(Constants.VERSION);
    private String organization = (String)config.get(Constants.ORGANIZATION);

    @SuppressWarnings("unchecked")
    private Map<String, Object> checkout = (Map<String, Object>)config.get(Constants.CHECKOUT);
    private String branch = (String)checkout.get(Constants.BRANCH);
    @SuppressWarnings("unchecked")
    private List<String> repositories = (List<String>)checkout.get(Constants.REPOSITORY);

    @SuppressWarnings("unchecked")
    private List<String> merges = (List<String>)config.get(Constants.MERGE);
    @SuppressWarnings("unchecked")
    private List<String> mavens = (List<String>)config.get(Constants.MAVEN);
    @SuppressWarnings("unchecked")
    private List<String> releases = (List<String>)config.get(Constants.RELEASE);
    @SuppressWarnings("unchecked")
    private List<String> dockers = (List<String>)config.get(Constants.DOCKER);
    private String userHome = System.getProperty("user.home");

    @Override
    public String getName() {
        return "dockerhub";
    }

    @Override
    public int execute() throws IOException, InterruptedException {
        int result = checkout();
        if(result != 0) return result;
        result = merge();
        if(result != 0) return result;
        result = maven();
        if(result != 0) return result;
        result = release();
        if(result != 0) return result;
        result = docker();
        return result;
    }

    private int checkout() throws IOException, InterruptedException {
        int result = 0;

        // check if there is a directory workspace in home directory.
        Path wPath = Paths.get(userHome, workspace);
        if(Files.notExists(wPath)) {
            Files.createDirectory(wPath);
        }

        for(String repository: repositories) {
            Path rPath = Paths.get(userHome, workspace, getDirFromRepo(repository));
            if(Files.notExists(rPath)) {
                // clone and switch to branch.
                CloneBranchCmd cloneBranchCmd = new CloneBranchCmd(repository, branch, wPath, rPath);
                result = cloneBranchCmd.execute();
                if(result != 0) break;
            } else {
                // switch to branch and pull
                CheckoutPullCmd checkoutPullCmd = new CheckoutPullCmd(branch, rPath);
                result = checkoutPullCmd.execute();
                if(result != 0) break;
            }
        }
        return result;
    }

    private int merge() throws IOException, InterruptedException {
        int result = 0;
        for(String merge: merges) {
            Path rPath = Paths.get(userHome, workspace, merge);
            MergeMasterCmd mergeMasterCmd = new MergeMasterCmd(rPath);
            result = mergeMasterCmd.execute();
            if(result != 0) break;
        }
        return result;
    }

    private int maven() throws IOException, InterruptedException {
        int result = 0;
        for(String maven: mavens) {
            Path rPath = Paths.get(userHome, workspace, maven);
            MavenBuildCmd mavenBuildCmd = new MavenBuildCmd(rPath);
            result = mavenBuildCmd.execute();
            if(result != 0) break;
        }
        return result;
    }

    private int release() throws IOException, InterruptedException {
        int result = 0;
        for(String release: releases) {
            Path rPath = Paths.get(userHome, workspace, release);
            // generate changelog.md, check in
            GenChangeLogCmd genChangeLogCmd = new GenChangeLogCmd(organization, release, version, rPath);
            result = genChangeLogCmd.execute();
            if(result != 0) break;

            // merge the changelog.md to develop and push
            MergeDevelopCmd mergeDevelopCmd = new MergeDevelopCmd(rPath);
            result = mergeDevelopCmd.execute();
            if(result != 0) break;

            // read CHANGELOG.md for the current release body.
            Charset charset = Charset.forName("UTF-8");
            Path file = Paths.get(userHome, workspace, release, "CHANGELOG.md");
            StringBuffer stringBuffer = new StringBuffer();
            try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
                String line;
                boolean tokenFound = false;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("## [" + version)) {
                        tokenFound = true;
                    } else if (line.startsWith("## [")) {
                        break;
                    }
                    if(tokenFound) {
                        stringBuffer.append(line);
                        stringBuffer.append("\n");
                    }
                }
            } catch (IOException e) {
                logger.error("IOException:", e);
            }

            // call github api to create a new release.
            GithubReleaseCmd githubReleaseCmd = new GithubReleaseCmd(organization, release, version, stringBuffer.toString(), rPath);
            result = githubReleaseCmd.execute();
            if(result != 0) break;
        }
        return result;
    }

    private int docker() throws IOException, InterruptedException {
        int result = 0;
        for(String docker: dockers) {
            Path rPath = Paths.get(userHome, workspace, docker);
            DockerBuildCmd dockerBuildCmd = new DockerBuildCmd(version, rPath);
            result = dockerBuildCmd.execute();
            if(result != 0) break;
        }
        return result;
    }

}