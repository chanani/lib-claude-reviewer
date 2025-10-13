package com.reviewer.config;

import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 리뷰 설정 클래스
 */
@Getter
@Builder
public class ReviewConfig {
    private final String githubToken;
    private final String anthropicApiKey;
    private final int prNumber;
    private final String repoName;

    @Builder.Default
    private final String model = "claude-sonnet-4-5-20250929";

    @Builder.Default
    private final String language = "ko";

    @Builder.Default
    private final List<String> fileExtensions = Arrays.asList(".java", ".kt", ".xml", ".gradle");

    @Builder.Default
    private final int maxTokens = 2000;

    /**
     * 환경 변수에서 설정 생성
     */
    public static ReviewConfig fromEnvironment() {
        String fileExts = getEnvOrDefault("FILE_EXTENSIONS", ".java,.kt,.xml,.gradle");

        return ReviewConfig.builder()
                .githubToken(getRequiredEnv("GITHUB_TOKEN"))
                .anthropicApiKey(getRequiredEnv("ANTHROPIC_API_KEY"))
                .prNumber(Integer.parseInt(getRequiredEnv("PR_NUMBER")))
                .repoName(getRequiredEnv("REPO_NAME"))
                .model(getEnvOrDefault("MODEL", "claude-sonnet-4-5-20250929"))
                .language(getEnvOrDefault("LANGUAGE", "ko"))
                .fileExtensions(Arrays.asList(fileExts.split(",")))
                .maxTokens(Integer.parseInt(getEnvOrDefault("MAX_TOKENS", "2000")))
                .build();
    }

    private static String getRequiredEnv(String key) {
        String value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException("Required environment variable not found: " + key);
        }
        return value;
    }

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
}
