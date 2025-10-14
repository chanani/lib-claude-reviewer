package com.reviewer.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.reviewer.config.ReviewConfig;
import com.reviewer.model.FileChange;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Claude AI 관련 서비스
 */
public class ClaudeService {
    private static final String ANTHROPIC_API_URL = "https://api.anthropic.com/v1/messages";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final ReviewConfig config;
    private final Gson gson;

    /**
     * 생성자 주입
     */
    public ClaudeService(ReviewConfig config) {
        this.config = config;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)    // 3분
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    /**
     * 시스템 프롬프트 생성
     */
    private String getSystemPrompt() {
        if ("ko".equals(config.getLanguage())) {
            return """
                    경험 많은 시니어 개발자로서 코드 리뷰를 수행해줘.

                    리뷰 지침:
                    1. 가장 중요한 문제점이나 개선사항에만 집중해.
                    2. 전체 변경사항에 대한 통합된 리뷰를 제공해.
                    3. 구체적인 개선 제안을 제시해.
                    4. 사소한 스타일 문제는 무시해.
                    5. 심각한 버그, 성능 문제, 보안 취약점만 언급해.
                    6. 이미 개선된 사항은 긍정적으로 언급해.

                    리뷰 형식:
                    - 개선된 사항: [긍정적 언급]
                    - 주요 이슈: [문제점과 개선 방안]
                    - 전반적인 의견: [요약]
                    """;
        } else {
            return """
                    As an experienced senior developer, perform a code review.

                    Review Guidelines:
                    1. Focus on the most important issues or improvements.
                    2. Provide an integrated review of all changes.
                    3. Suggest specific improvements.
                    4. Ignore minor style issues.
                    5. Only mention critical bugs, performance issues, or security vulnerabilities.
                    6. Positively mention already improved aspects.

                    Review Format:
                    - Improvements: [positive mentions]
                    - Key Issues: [problems and suggestions]
                    - Overall Opinion: [summary]
                    """;
        }
    }

    /**
     * 변경사항을 텍스트로 포맷팅
     */
    private String formatChanges(List<FileChange> changes) {
        StringBuilder sb = new StringBuilder();
        for (FileChange change : changes) {
            sb.append("\n파일: ").append(change.getFilename())
                    .append(" (").append(change.getStatus()).append(")\n");
            sb.append(change.getPatch()).append("\n");
            sb.append("-".repeat(80)).append("\n");
        }
        return sb.toString();
    }

    /**
     * 코드 리뷰 수행
     */
    public String reviewCode(List<FileChange> changes) throws IOException {
        String changesText = formatChanges(changes);
        String systemPrompt = getSystemPrompt();

        String userPrompt = "ko".equals(config.getLanguage())
                ? "다음 변경사항을 리뷰해줘:\n\n"
                : "Please review the following changes:\n\n";

        // Request body 구성
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", config.getModel());
        requestBody.addProperty("max_tokens", config.getMaxTokens());
        requestBody.addProperty("system", systemPrompt);

        JsonArray messages = new JsonArray();
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", userPrompt + changesText);
        messages.add(userMessage);
        requestBody.add("messages", messages);

        // API 호출
        Request request = new Request.Builder()
                .url(ANTHROPIC_API_URL)
                .addHeader("x-api-key", config.getAnthropicApiKey())
                .addHeader("anthropic-version", "2023-06-01")
                .addHeader("content-type", "application/json")
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("API 호출 실패: " + response);
            }

            JsonObject responseBody = gson.fromJson(response.body().string(), JsonObject.class);
            return responseBody.getAsJsonArray("content")
                    .get(0).getAsJsonObject()
                    .get("text").getAsString();
        }
    }
}
