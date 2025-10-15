package com.reviewer.di;

import com.reviewer.config.ReviewConfig;
import com.reviewer.service.*;

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
     * GitService 인스턴스 생성
     * 플랫폼에 따라 GitHubServiceImpl 또는 GiteaServiceImpl 반환
     */
    public GitService createGitService() throws IOException {
        if (config.isGitea()) {
            return new GiteaServiceImpl(config);
        } else {
            return new GitHubServiceImpl(config);
        }
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
        GitService gitService = createGitService();
        ClaudeService claudeService = createClaudeService();
        return new ReviewService(gitService, claudeService);
    }
}
