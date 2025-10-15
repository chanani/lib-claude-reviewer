package com.reviewer.service;

import com.reviewer.model.FileChange;

import java.io.IOException;
import java.util.List;

/**
 * 리뷰 프로세스를 조율하는 Facade 서비스
 */
public class ReviewService {
    private final GitService gitService;
    private final ClaudeService claudeService;

    /**
     * 생성자 주입
     */
    public ReviewService(GitService gitService, ClaudeService claudeService) {
        this.gitService = gitService;
        this.claudeService = claudeService;
    }

    /**
     * 리뷰 프로세스 실행
     */
    public void executeReview() throws IOException {
        System.out.println("🔍 변경된 파일 확인 중...");

        // 1. 변경된 파일 가져오기
        List<FileChange> changedFiles = gitService.getChangedFiles();

        if (changedFiles.isEmpty()) {
            System.out.println("ℹ️ 리뷰할 파일이 없습니다.");
            return;
        }

        System.out.println("📝 " + changedFiles.size() + "개 파일 발견");

        // 2. Claude AI로 리뷰 수행
        System.out.println("🤖 AI 리뷰 진행 중...");
        String reviewText = claudeService.reviewCode(changedFiles);

        // 3. PR에 댓글 작성
        System.out.println("💬 리뷰 결과 게시 중...");
        gitService.postComment("## 🤖 Claude AI Code Review\n\n" + reviewText);

        System.out.println("✅ 리뷰 완료!");
    }
}
