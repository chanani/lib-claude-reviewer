package com.reviewer.di;

import com.reviewer.config.ReviewConfig;
import com.reviewer.service.ClaudeService;
import com.reviewer.service.GitHubService;
import com.reviewer.service.ReviewService;

import java.io.IOException;

/**
 * DI Container - 서비스 인스턴스 생성 및 의존성 주입
 */
public class ServiceFactory {
    private final ReviewConfig config;

    public ServiceFactory(ReviewConfig config) {
        this.config = config;
    }

    /**
     * GitHubService 인스턴스 생성
     */
    public GitHubService createGitHubService() throws IOException {
        return new GitHubService(config);
    }

    /**
     * ClaudeService 인스턴스 생성
     */
    public ClaudeService createClaudeService() {
        return new ClaudeService(config);
    }

    /**
     * ReviewService 인스턴스 생성 (의존성 주입)
     */
    public ReviewService createReviewService() throws IOException {
        GitHubService githubService = createGitHubService();
        ClaudeService claudeService = createClaudeService();
        return new ReviewService(githubService, claudeService);
    }
}
