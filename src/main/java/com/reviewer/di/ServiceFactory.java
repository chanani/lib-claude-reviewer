package com.reviewer.di;

import com.reviewer.config.ReviewConfig;
import com.reviewer.service.*;
import com.reviewer.service.claude.ClaudeService;
import com.reviewer.service.git.GitHubServiceImpl;
import com.reviewer.service.git.GitService;
import com.reviewer.service.git.GiteaServiceImpl;

import java.io.IOException;

/**
 * DI Container - 서비스 인스턴스 생성 및 의존성 주입
 */
public class ServiceFactory {
    private final ReviewConfig config;

    /**
     * ServiceFactory 생성자
     *
     * @param config 리뷰 설정
     */
    public ServiceFactory(ReviewConfig config) {
        this.config = config;
    }

    /**
     * GitService 인스턴스 생성
     * 플랫폼에 따라 GitHubServiceImpl 또는 GiteaServiceImpl 반환
     *
     * @return GitService 인스턴스
     * @throws IOException API 연결 실패 시
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
     *
     * @return ClaudeService 인스턴스
     */
    public ClaudeService createClaudeService() {
        return new ClaudeService(config);
    }

    /**
     * ReviewService 인스턴스 생성 (의존성 주입)
     *
     * @return ReviewService 인스턴스
     * @throws IOException API 연결 실패 시
     */
    public ReviewService createReviewService() throws IOException {
        GitService gitService = createGitService();
        ClaudeService claudeService = createClaudeService();
        return new ReviewService(gitService, claudeService);
    }
}
