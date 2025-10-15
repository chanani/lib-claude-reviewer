package com.reviewer.service;

import com.reviewer.model.FileChange;

import java.io.IOException;
import java.util.List;

/**
 * Git 플랫폼 서비스 인터페이스
 * GitHub, Gitea 등 다양한 플랫폼 지원
 */
public interface GitService {

    /**
     * PR에서 변경된 파일 목록 조회
     */
    List<FileChange> getChangedFiles() throws IOException;

    /**
     * PR에 댓글 작성
     */
    void postComment(String comment) throws IOException;
}
