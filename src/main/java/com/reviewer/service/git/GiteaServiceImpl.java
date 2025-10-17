package com.reviewer.service.git;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.reviewer.config.ReviewConfig;
import com.reviewer.model.FileChange;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Gitea 서비스 구현체
 */
public class GiteaServiceImpl implements GitService {
    private final OkHttpClient client;
    private final ReviewConfig config;
    private final Gson gson;
    private final String baseUrl;

    /**
     * 생성자 주입
     *
     * @param config 리뷰 설정
     */
    public GiteaServiceImpl(ReviewConfig config) {
        this.config = config;
        this.baseUrl = config.getGiteaUrl();
        this.gson = new Gson();
        this.client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 변경된 파일 목록 가져오기
     */
    @Override
    public List<FileChange> getChangedFiles() throws IOException {
        String url = String.format("%s/api/v1/repos/%s/pulls/%d/files",
                baseUrl, config.getRepoName(), config.getPrNumber());

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "token " + config.getGithubToken())
                .header("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Gitea API 호출 실패: " + response.code() + " " + response.message());
            }

            String responseBody = response.body().string();
            JsonArray filesArray = gson.fromJson(responseBody, JsonArray.class);

            List<FileChange> changes = new ArrayList<>();
            for (int i = 0; i < filesArray.size(); i++) {
                JsonObject fileObj = filesArray.get(i).getAsJsonObject();

                String filename = fileObj.get("filename").getAsString();
                String patch = fileObj.has("patch") && !fileObj.get("patch").isJsonNull()
                        ? fileObj.get("patch").getAsString()
                        : "";
                String status = fileObj.get("status").getAsString();

                FileChange fileChange = new FileChange(filename, patch, status);

                if (fileChange.matchesExtensions(config.getFileExtensions())) {
                    changes.add(fileChange);
                }
            }

            return changes;
        }
    }

    /**
     * PR에 댓글 작성
     */
    @Override
    public void postComment(String comment) throws IOException {
        String url = String.format("%s/api/v1/repos/%s/issues/%d/comments",
                baseUrl, config.getRepoName(), config.getPrNumber());

        JsonObject body = new JsonObject();
        body.addProperty("body", comment);

        RequestBody requestBody = RequestBody.create(
                gson.toJson(body),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "token " + config.getGithubToken())
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Gitea 댓글 작성 실패: " + response.code() + " " + response.message());
            }
        }
    }
}
