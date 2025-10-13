package com.reviewer;

import com.reviewer.config.ReviewConfig;
import com.reviewer.di.ServiceFactory;
import com.reviewer.model.FileChange;
import com.reviewer.service.ClaudeService;
import com.reviewer.service.GitHubService;
import com.reviewer.service.ReviewService;

import java.io.IOException;
import java.util.List;

/**
 * Claude PR Reviewer의 메인 API 클래스
 *
 * 사용 예시:
 * <pre>{@code
 * ClaudeReviewer reviewer = ClaudeReviewer.builder()
 *     .githubToken("ghp_xxx")
 *     .anthropicApiKey("sk-ant-xxx")
 *     .repoName("owner/repo")
 *     .prNumber(123)
 *     .build();
 *
 * String review = reviewer.reviewPullRequest();
 * reviewer.postReviewComment(review);
 * }</pre>
 */
public class ClaudeReviewer {
    private final GitHubService githubService;
    private final ClaudeService claudeService;
    private final ReviewService reviewService;

    private ClaudeReviewer(ReviewConfig config) throws IOException {
        ServiceFactory factory = new ServiceFactory(config);
        this.githubService = factory.createGitHubService();
        this.claudeService = factory.createClaudeService();
        this.reviewService = factory.createReviewService();
    }

    /**
     * PR 리뷰를 수행하고 결과를 반환
     *
     * @return 리뷰 결과 텍스트
     * @throws IOException API 호출 실패 시
     */
    public String reviewPullRequest() throws IOException {
        List<FileChange> changedFiles = githubService.getChangedFiles();

        if (changedFiles.isEmpty()) {
            return "리뷰할 파일이 없습니다.";
        }

        return claudeService.reviewCode(changedFiles);
    }

    /**
     * PR에 리뷰 댓글 작성
     *
     * @param reviewComment 작성할 댓글 내용
     * @throws IOException API 호출 실패 시
     */
    public void postReviewComment(String reviewComment) throws IOException {
        githubService.postComment("## 🤖 Claude AI Code Review\n\n" + reviewComment);
    }

    /**
     * 변경된 파일 목록 조회
     *
     * @return 변경된 파일 목록
     * @throws IOException API 호출 실패 시
     */
    public List<FileChange> getChangedFiles() throws IOException {
        return githubService.getChangedFiles();
    }

    /**
     * PR 리뷰를 수행하고 자동으로 댓글 작성
     *
     * @throws IOException API 호출 실패 시
     */
    public void executeFullReview() throws IOException {
        reviewService.executeReview();
    }

    /**
     * ClaudeReviewer 빌더
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String githubToken;
        private String anthropicApiKey;
        private int prNumber;
        private String repoName;
        private String model = "claude-sonnet-4-5-20250929";
        private String language = "ko";
        private String fileExtensions = ".java,.kt,.xml,.gradle";
        private int maxTokens = 2000;

        public Builder githubToken(String githubToken) {
            this.githubToken = githubToken;
            return this;
        }

        public Builder anthropicApiKey(String anthropicApiKey) {
            this.anthropicApiKey = anthropicApiKey;
            return this;
        }

        public Builder prNumber(int prNumber) {
            this.prNumber = prNumber;
            return this;
        }

        public Builder repoName(String repoName) {
            this.repoName = repoName;
            return this;
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder language(String language) {
            this.language = language;
            return this;
        }

        public Builder fileExtensions(String fileExtensions) {
            this.fileExtensions = fileExtensions;
            return this;
        }

        public Builder maxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }

        public ClaudeReviewer build() throws IOException {
            // 환경 변수 설정 (ReviewConfig.fromEnvironment()에서 사용)
            System.setProperty("GITHUB_TOKEN", githubToken);
            System.setProperty("ANTHROPIC_API_KEY", anthropicApiKey);
            System.setProperty("PR_NUMBER", String.valueOf(prNumber));
            System.setProperty("REPO_NAME", repoName);
            System.setProperty("MODEL", model);
            System.setProperty("LANGUAGE", language);
            System.setProperty("FILE_EXTENSIONS", fileExtensions);
            System.setProperty("MAX_TOKENS", String.valueOf(maxTokens));

            ReviewConfig config = ReviewConfig.builder()
                    .githubToken(githubToken)
                    .anthropicApiKey(anthropicApiKey)
                    .prNumber(prNumber)
                    .repoName(repoName)
                    .model(model)
                    .language(language)
                    .fileExtensions(java.util.Arrays.asList(fileExtensions.split(",")))
                    .maxTokens(maxTokens)
                    .build();

            return new ClaudeReviewer(config);
        }
    }
}
