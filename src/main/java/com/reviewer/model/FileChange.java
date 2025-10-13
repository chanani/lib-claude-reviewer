package com.reviewer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 파일 변경 정보 DTO
 */
@Getter
@AllArgsConstructor
public class FileChange {
    private final String filename;
    private final String patch;
    private final String status;

    /**
     * 파일이 지정된 확장자와 매칭되는지 확인
     */
    public boolean matchesExtensions(List<String> extensions) {
        return extensions.stream()
                .anyMatch(ext -> filename.endsWith(ext.trim()));
    }
}
