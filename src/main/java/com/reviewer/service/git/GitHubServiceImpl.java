package com.reviewer.service.git;

import com.reviewer.config.ReviewConfig;
import com.reviewer.model.FileChange;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestFileDetail;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * GitHub 서비스 구현체
 */
public class GitHubServiceImpl implements GitService {
    private final GitHub github;
    private final ReviewConfig config;

    /**
     * 생성자 주입
     */
    public GitHubServiceImpl(ReviewConfig config) throws IOException {
        this.config = config;
        this.github = new GitHubBuilder()
                .withOAuthToken(config.getGithubToken())
                .build();
    }

    /**
     * PR 객체 가져오기
     */
    private GHPullRequest getPullRequest() throws IOException {
        GHRepository repo = github.getRepository(config.getRepoName());
        return repo.getPullRequest(config.getPrNumber());
    }

    /**
     * 변경된 파일 목록 가져오기
     */
    @Override
    public List<FileChange> getChangedFiles() throws IOException {
        GHPullRequest pr = getPullRequest();
        List<FileChange> changes = new ArrayList<>();

        for (GHPullRequestFileDetail file : pr.listFiles()) {
            FileChange fileChange = new FileChange(
                    file.getFilename(),
                    file.getPatch() != null ? file.getPatch() : "",
                    file.getStatus()
            );

            if (fileChange.matchesExtensions(config.getFileExtensions())) {
                changes.add(fileChange);
            }
        }

        return changes;
    }

    /**
     * PR에 댓글 작성
     */
    @Override
    public void postComment(String comment) throws IOException {
        GHPullRequest pr = getPullRequest();
        pr.comment(comment);
    }
}
