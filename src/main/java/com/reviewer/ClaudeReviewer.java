package com.reviewer;

import com.reviewer.config.ReviewConfig;
import com.reviewer.di.ServiceFactory;
import com.reviewer.model.FileChange;
import com.reviewer.service.claude.ClaudeService;
import com.reviewer.service.git.GitService;
import com.reviewer.service.ReviewService;

import java.io.IOException;
import java.util.List;

/**
 * Claude PR Reviewer의 메인 API 클래스
 *
 * 사용 예시:
 * <pre>{@code
 * // GitHub
 * ClaudeReviewer reviewer = ClaudeReviewer.builder()
 *     .githubToken("ghp_xxx")
 *     .anthropicApiKey("sk-ant-xxx")
 *     .repoName("owner/repo")
 *     .prNumber(123)
 *     .build();
 *
 * // Gitea
 * ClaudeReviewer reviewer = ClaudeReviewer.builder()
 *     .giteaUrl("https://gitea.example.com")
 *     .githubToken("gitea_token")
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
    private final GitService gitService;
    private final ClaudeService claudeService;
    private final ReviewService reviewService;

    private ClaudeReviewer(ReviewConfig config) throws IOException {
        ServiceFactory factory = new ServiceFactory(config);
        this.gitService = factory.createGitService();
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
        List<FileChange> changedFiles = gitService.getChangedFiles();

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
        gitService.postComment("## 🤖 Claude AI Code Review\n\n" + reviewComment);
    }

    /**
     * 변경된 파일 목록 조회
     *
     * @return 변경된 파일 목록
     * @throws IOException API 호출 실패 시
     */
    public List<FileChange> getChangedFiles() throws IOException {
        return gitService.getChangedFiles();
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
     * ClaudeReviewer 빌더 인스턴스 생성
     *
     * @return Builder 인스턴스
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * ClaudeReviewer 빌더 클래스
     */
    public static class Builder {
        /**
         * Builder 기본 생성자
         */
        Builder() {
        }

        private String githubToken;
        private String anthropicApiKey;
        private int prNumber;
        private String repoName;
        private String giteaUrl;  // Gitea 지원
        private String model = "claude-sonnet-4-5-20250929";
        private String language = "ko";
        private String fileExtensions = ".java,.kt,.xml,.gradle";
        private int maxTokens = 2000;

        /**
         * GitHub 또는 Gitea Access Token 설정
         *
         * @param githubToken GitHub 또는 Gitea Access Token
         * @return Builder 인스턴스
         */
        public Builder githubToken(String githubToken) {
            this.githubToken = githubToken;
            return this;
        }

        /**
         * Anthropic API Key 설정
         *
         * @param anthropicApiKey Claude API Key
         * @return Builder 인스턴스
         */
        public Builder anthropicApiKey(String anthropicApiKey) {
            this.anthropicApiKey = anthropicApiKey;
            return this;
        }

        /**
         * Pull Request 번호 설정
         *
         * @param prNumber PR 번호
         * @return Builder 인스턴스
         */
        public Builder prNumber(int prNumber) {
            this.prNumber = prNumber;
            return this;
        }

        /**
         * 저장소 이름 설정 (예: "owner/repo")
         *
         * @param repoName 저장소 이름
         * @return Builder 인스턴스
         */
        public Builder repoName(String repoName) {
            this.repoName = repoName;
            return this;
        }

        /**
         * Gitea 서버 URL 설정 (Gitea 사용 시만 필요)
         *
         * @param giteaUrl Gitea 서버 URL
         * @return Builder 인스턴스
         */
        public Builder giteaUrl(String giteaUrl) {
            this.giteaUrl = giteaUrl;
            return this;
        }

        /**
         * Claude 모델 설정
         *
         * @param model Claude 모델명
         * @return Builder 인스턴스
         */
        public Builder model(String model) {
            this.model = model;
            return this;
        }

        /**
         * 리뷰 언어 설정
         *
         * @param language 언어 코드 ("ko" 또는 "en")
         * @return Builder 인스턴스
         */
        public Builder language(String language) {
            this.language = language;
            return this;
        }

        /**
         * 리뷰할 파일 확장자 설정
         *
         * @param fileExtensions 파일 확장자 (쉼표로 구분)
         * @return Builder 인스턴스
         */
        public Builder fileExtensions(String fileExtensions) {
            this.fileExtensions = fileExtensions;
            return this;
        }

        /**
         * 최대 토큰 수 설정
         *
         * @param maxTokens 최대 토큰 수
         * @return Builder 인스턴스
         */
        public Builder maxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }

        /**
         * ClaudeReviewer 인스턴스 생성
         *
         * @return ClaudeReviewer 인스턴스
         * @throws IOException API 연결 실패 시
         */
        public ClaudeReviewer build() throws IOException {
            // 환경 변수에서 자동 감지 (설정 안 된 경우)
            if (githubToken == null) {
                githubToken = System.getenv("GITHUB_TOKEN");
            }
            if (anthropicApiKey == null) {
                anthropicApiKey = System.getenv("ANTHROPIC_API_KEY");
            }
            if (prNumber == 0) {
                String prEnv = System.getenv("PR_NUMBER");
                if (prEnv != null) {
                    prNumber = Integer.parseInt(prEnv);
                }
            }
            if (repoName == null) {
                repoName = System.getenv("REPO_NAME");
            }
            if (giteaUrl == null) {
                giteaUrl = System.getenv("GITEA_URL");
            }

            // 필수 값 검증
            if (githubToken == null || githubToken.isEmpty()) {
                throw new IllegalStateException("GitHub token is required");
            }
            if (anthropicApiKey == null || anthropicApiKey.isEmpty()) {
                throw new IllegalStateException("Anthropic API key is required");
            }
            if (prNumber == 0) {
                throw new IllegalStateException("PR number is required");
            }
            if (repoName == null || repoName.isEmpty()) {
                throw new IllegalStateException("Repository name is required");
            }

            ReviewConfig config = ReviewConfig.builder()
                    .githubToken(githubToken)
                    .anthropicApiKey(anthropicApiKey)
                    .prNumber(prNumber)
                    .repoName(repoName)
                    .giteaUrl(giteaUrl)
                    .model(model)
                    .language(language)
                    .fileExtensions(java.util.Arrays.asList(fileExtensions.split(",")))
                    .maxTokens(maxTokens)
                    .build();

            return new ClaudeReviewer(config);
        }
    }
}
